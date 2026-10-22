package co.dazma.api.publications.services.usecases;

import co.dazma.api.publications.domain.*;
import co.dazma.api.publications.services.input.dto.CreatePublicationInputDto;
import co.dazma.api.publications.services.input.ports.CreatePublicationUseCase;
import co.dazma.api.publications.services.output.models.StoredPublicationPhoto;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import co.dazma.api.publications.services.output.ports.PublicationRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CreatePublicationService implements CreatePublicationUseCase {

    private final PublicationRepositoryPort publicationRepository;
    private final PublicationPhotoStoragePort photoStorage;
    private final Clock clock;

    public CreatePublicationService(
            PublicationRepositoryPort publicationRepository,
            PublicationPhotoStoragePort photoStorage,
            Clock clock
    ) {
        this.publicationRepository = Objects.requireNonNull(
                publicationRepository,
                "El repositorio de publicaciones es obligatorio"
        );
        this.photoStorage = Objects.requireNonNull(
                photoStorage,
                "El almacenamiento de fotografías es obligatorio"
        );
        this.clock = Objects.requireNonNull(clock, "El reloj es obligatorio");
    }

    @Override
    @Transactional
    public Publication create(CreatePublicationInputDto input) {

        List<PublicationPhoto> photos = resolvePhotos(
                input.ownerId(),
                input.photoIds()
        );

        Publication publication = Publication.publish(
                input.ownerId(),
                new Name(input.name()),
                input.type(),
                new Description(input.description()),
                new Municipality(input.municipality()),
                photos,
                clock.instant(),
                new Price(input.price())
        );

        return publicationRepository.save(publication);
    }

    private List<PublicationPhoto> resolvePhotos(
            UUID ownerId,
            List<UUID> photoIds
    ) {
        List<PublicationPhoto> photos = new ArrayList<>(photoIds.size());
        for (int position = 0; position < photoIds.size(); position++) {
            UUID photoId = photoIds.get(position);

            StoredPublicationPhoto storedPhoto = photoStorage
                    .findUploadedPhoto(ownerId, photoId)
                    .orElseThrow(() -> new PublicationPhotoNotAvailableException(photoId));

            photos.add(new PublicationPhoto(
                    storedPhoto.photoId(),
                    storedPhoto.objectKey(),
                    position
            ));
        }
        return List.copyOf(photos);
    }
}
