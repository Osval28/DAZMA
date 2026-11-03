package co.dazma.api.publications.services.usecases.impl;

import co.dazma.api.publications.domain.models.Publication;
import co.dazma.api.publications.domain.exceptions.PublicationNotFoundException;
import co.dazma.api.publications.domain.models.PublicationPhoto;
import co.dazma.api.publications.services.output.ports.PhotoDownloadParametersPort;
import co.dazma.api.publications.services.output.ports.PublicationCachePort;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import co.dazma.api.publications.services.output.ports.PublicationRepositoryPort;
import co.dazma.api.publications.services.usecases.contract.GetPublicationUseCase;
import co.dazma.api.publications.services.usecases.models.PhotoAccess;
import co.dazma.api.publications.services.usecases.models.PublicationDetail;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
        this.publicationCache = publicationCache;
        this.publicationRepository = publicationRepository;
        this.photoStorage = photoStorage;
        this.downloadParameters = downloadParameters;
    }

    @Override
    public PublicationDetail execute(UUID publicationId) {
        Publication publication = publicationCache.findById(publicationId)
                .orElseGet(() -> loadFromRepositoryAndCache(publicationId));

        return withPhotoAccess(publication);
    }

    private Publication loadFromRepositoryAndCache(UUID publicationId) {
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new PublicationNotFoundException(publicationId));
        publicationCache.save(publication);
        return publication;
    }

    private PublicationDetail withPhotoAccess(Publication publication) {
        Duration downloadValidity = downloadParameters.downloadValidity();
        return new PublicationDetail(
                publication,
                publication.getPhotos().stream()
                        .map(photo -> toPhotoAccess(photo, downloadValidity))
                        .toList()
        );
    }

    private PhotoAccess toPhotoAccess(PublicationPhoto photo, Duration validity) {
        return new PhotoAccess(
                photo.getId(),
                photoStorage.authorizeDownload(photo.getObjectKey(), validity),
                photo.getPosition()
        );
    }
}
