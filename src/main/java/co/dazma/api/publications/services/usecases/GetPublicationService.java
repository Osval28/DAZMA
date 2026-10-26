package co.dazma.api.publications.services.usecases;

import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationNotFoundException;
import co.dazma.api.publications.domain.PublicationPhoto;
import co.dazma.api.publications.services.input.dto.GetPublicationOutputDto;
import co.dazma.api.publications.services.input.dto.GetPublicationPhotoOutputDto;
import co.dazma.api.publications.services.input.ports.GetPublicationUseCase;
import co.dazma.api.publications.services.output.ports.PhotoDownloadParametersPort;
import co.dazma.api.publications.services.output.ports.PublicationCachePort;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import co.dazma.api.publications.services.output.ports.PublicationRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Service
public class GetPublicationService implements GetPublicationUseCase {

    private final PublicationCachePort publicationCache;
    private final PublicationRepositoryPort publicationRepository;
    private final PublicationPhotoStoragePort photoStorage;
    private final PhotoDownloadParametersPort downloadParameters;

    public GetPublicationService(
            PublicationCachePort publicationCache,
            PublicationRepositoryPort publicationRepository,
            PublicationPhotoStoragePort photoStorage,
            PhotoDownloadParametersPort downloadParameters
    ) {
        this.publicationCache = Objects.requireNonNull(
                publicationCache,
                "La caché de publicaciones es obligatoria"
        );
        this.publicationRepository = Objects.requireNonNull(
                publicationRepository,
                "El repositorio de publicaciones es obligatorio"
        );
        this.photoStorage = Objects.requireNonNull(
                photoStorage,
                "El almacenamiento de fotografías es obligatorio"
        );
        this.downloadParameters = Objects.requireNonNull(
                downloadParameters,
                "Los parámetros de descarga de fotografías son obligatorios"
        );
    }

    @Override
    public GetPublicationOutputDto getById(UUID publicationId) {
        Publication publication = publicationCache.findById(publicationId)
                .orElseGet(() -> loadFromRepositoryAndCache(publicationId));

        return toOutput(publication);
    }

    private Publication loadFromRepositoryAndCache(UUID publicationId) {
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new PublicationNotFoundException(publicationId));
        publicationCache.save(publication);
        return publication;
    }

    private GetPublicationOutputDto toOutput(Publication publication) {
        Duration downloadValidity = downloadParameters.downloadValidity();
        return new GetPublicationOutputDto(
                publication.getId(),
                publication.getOwnerId(),
                publication.getName().name(),
                publication.getType(),
                publication.getDescription().text(),
                publication.getMunicipality().name(),
                publication.getStatus(),
                publication.getCreatedAt(),
                publication.getPrice().value(),
                publication.getPhotos().stream()
                        .map(photo -> toPhotoOutput(photo, downloadValidity))
                        .toList()
        );
    }

    private GetPublicationPhotoOutputDto toPhotoOutput(PublicationPhoto photo, Duration validity) {
        return new GetPublicationPhotoOutputDto(
                photo.getId(),
                photoStorage.authorizeDownload(photo.getObjectKey(), validity),
                photo.getPosition()
        );
    }
}
