package co.dazma.api.publications.services.usecases.impl;

import co.dazma.api.publications.domain.*;
import co.dazma.api.publications.events.PublicationCreated;
import co.dazma.api.publications.services.output.models.StoredPublicationPhoto;
import co.dazma.api.publications.services.output.ports.PublicationEventsPort;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import co.dazma.api.publications.services.output.ports.PublicationRepositoryPort;
import co.dazma.api.publications.services.usecases.contract.CreatePublicationUseCase;
import co.dazma.api.shared.domain.errors.InvalidMunicipalityException;
import co.dazma.api.shared.domain.errors.MunicipalityErrorCode;
import co.dazma.api.shared.services.output.ports.MunicipalityCatalogPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CreatePublicationService implements CreatePublicationUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreatePublicationService.class);

    private final OperatingOwnerResolver operatingOwners;
    private final MunicipalityCatalogPort municipalityCatalog;
    private final PublicationRepositoryPort publicationRepository;
    private final PublicationPhotoStoragePort photoStorage;
    private final PublicationEventsPort publicationEvents;
    private final TransactionOperations transaction;
    private final Clock clock;

    public CreatePublicationService(
            OperatingOwnerResolver operatingOwners,
            MunicipalityCatalogPort municipalityCatalog,
            PublicationRepositoryPort publicationRepository,
            PublicationPhotoStoragePort photoStorage,
            PublicationEventsPort publicationEvents,
            TransactionOperations transaction,
            Clock clock
    ) {
        this.operatingOwners = operatingOwners;
        this.municipalityCatalog = municipalityCatalog;
        this.publicationRepository = publicationRepository;
        this.photoStorage = photoStorage;
        this.publicationEvents = publicationEvents;
        this.transaction = transaction;
        this.clock = clock;
    }

    @Override
    public Publication execute(PublicationDraft draft) {
        if (draft.photoIds().size() > Publication.MAX_PHOTOS) {
            throw new InvalidPublicationException(PublicationErrorCode.PHOTOS_LIMIT_EXCEEDED, Publication.MAX_PHOTOS);
        }
        if (!draft.ownerEmailVerified()) {
            throw new PublicationOwnerEmailNotVerifiedException();
        }
        if (!municipalityCatalog.exists(draft.municipality())) {
            throw new InvalidMunicipalityException(MunicipalityErrorCode.MUNICIPALITY_NOT_FOUND);
        }

        UUID ownerId = operatingOwners.resolveId(draft.ownerExternalId());

        List<String> promotedObjectKeys = new ArrayList<>();
        Publication savedPublication;
        try {
            List<PublicationPhoto> photos = promotePhotos(ownerId, draft.photoIds(), promotedObjectKeys);

            Publication publication = Publication.publish(
                    ownerId,
                    operatingOwners.resolveName(draft.ownerExternalId()),
                    draft.name(),
                    draft.type(),
                    draft.description(),
                    draft.municipality(),
                    photos,
                    clock.instant(),
                    draft.price()
            );

            savedPublication = transaction.execute(status -> saveAndAnnounce(publication));
        } catch (RuntimeException failure) {
            discardPromotedPhotos(promotedObjectKeys, failure);
            throw failure;
        }

        log.atInfo()
                .addKeyValue("event.name", "publication.created")
                .addKeyValue("publication.id", savedPublication.getId())
                .addKeyValue("publication.status", savedPublication.getStatus().name())
                .log("Publicación creada");
        return savedPublication;
    }

    private void discardPromotedPhotos(List<String> objectKeys, RuntimeException failure) {
        if (objectKeys.isEmpty()) {
            return;
        }
        try {
            photoStorage.discardPhotos(List.copyOf(objectKeys));
        } catch (RuntimeException discardFailure) {
            failure.addSuppressed(discardFailure);
        }
    }

    private Publication saveAndAnnounce(Publication publication) {
        Publication savedPublication = publicationRepository.save(publication);
        publicationEvents.publish(new PublicationCreated(
                savedPublication.getId(),
                savedPublication.getOwnerId(),
                savedPublication.getCreatedAt()
        ));
        return savedPublication;
    }

    private List<PublicationPhoto> promotePhotos(
            UUID ownerId,
            List<UUID> photoIds,
            List<String> promotedObjectKeys
    ) {
        List<PublicationPhoto> photos = new ArrayList<>(photoIds.size());
        for (int position = 0; position < photoIds.size(); position++) {
            UUID photoId = photoIds.get(position);

            StoredPublicationPhoto storedPhoto = photoStorage
                    .promoteUploadedPhoto(ownerId, photoId)
                    .orElseThrow(() -> new PublicationPhotoNotAvailableException(photoId));
            promotedObjectKeys.add(storedPhoto.objectKey());

            photos.add(new PublicationPhoto(
                    storedPhoto.photoId(),
                    storedPhoto.objectKey(),
                    position
            ));
        }
        return List.copyOf(photos);
    }
}
