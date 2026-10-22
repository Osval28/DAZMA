package co.dazma.api.publications.services.usecases;

import co.dazma.api.publications.domain.*;
import co.dazma.api.publications.services.input.models.CreatePublicationCommand;
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
    public Publication create(CreatePublicationCommand command) {

        List<PublicationPhoto> photos = resolvePhotos(
                command.ownerId(),
                command.photoIds()
        );

        Publication publication = Publication.publish(
                command.ownerId(),
                command.name(),
                command.type(),
                command.description(),
                new Municipality(command.municipality()),
                photos,
                clock.instant(),
                new Price(command.price())
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
