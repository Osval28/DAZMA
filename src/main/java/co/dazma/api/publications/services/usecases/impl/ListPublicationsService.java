package co.dazma.api.publications.services.usecases.impl;

import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationPhoto;
import co.dazma.api.publications.services.output.models.PublishedPublicationsSlice;
import co.dazma.api.publications.services.output.ports.PhotoDownloadParametersPort;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import co.dazma.api.publications.services.output.ports.PublicationRepositoryPort;
import co.dazma.api.publications.services.usecases.contract.ListPublicationsUseCase;
import co.dazma.api.publications.services.usecases.models.PhotoAccess;
import co.dazma.api.publications.services.usecases.models.PublicationSearchCriteria;
import co.dazma.api.publications.services.usecases.models.PublicationSummary;
import co.dazma.api.publications.services.usecases.models.PublicationSummaryPage;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class ListPublicationsService implements ListPublicationsUseCase {

    private final PublicationRepositoryPort publicationRepository;
    private final PublicationPhotoStoragePort photoStorage;
    private final PhotoDownloadParametersPort downloadParameters;

    public ListPublicationsService(
            PublicationRepositoryPort publicationRepository,
            PublicationPhotoStoragePort photoStorage,
            PhotoDownloadParametersPort downloadParameters
    ) {
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
    public PublicationSummaryPage execute(PublicationSearchCriteria criteria) {
        PublishedPublicationsSlice slice = publicationRepository.findPublished(
                criteria.municipality(),
                criteria.page(),
                criteria.size()
        );
        Duration downloadValidity = downloadParameters.downloadValidity();

        List<PublicationSummary> summaries = slice.publications().stream()
                .map(publication -> new PublicationSummary(publication, coverOf(publication, downloadValidity)))
                .toList();

        return new PublicationSummaryPage(summaries, criteria.page(), criteria.size(), slice.hasNext());
    }

    private PhotoAccess coverOf(Publication publication, Duration validity) {
        // Una publicación siempre tiene al menos una foto (lo garantiza el dominio).
        PublicationPhoto cover = publication.getPhotos().stream()
                .min(Comparator.comparingInt(PublicationPhoto::getPosition))
                .orElseThrow();
        return new PhotoAccess(
                cover.getId(),
                photoStorage.authorizeDownload(cover.getObjectKey(), validity),
                cover.getPosition()
        );
    }
}
