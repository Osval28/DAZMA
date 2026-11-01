package co.dazma.api.publications.services.usecases.impl;

import co.dazma.api.publications.domain.Description;
import co.dazma.api.publications.domain.Name;
import co.dazma.api.publications.domain.Price;
import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationPhoto;
import co.dazma.api.publications.domain.PublicationStatus;
import co.dazma.api.publications.domain.PublicationType;
import co.dazma.api.publications.services.output.models.PublishedPublicationsSlice;
import co.dazma.api.publications.services.output.ports.PhotoDownloadParametersPort;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import co.dazma.api.publications.services.output.ports.PublicationRepositoryPort;
import co.dazma.api.publications.services.usecases.models.PublicationSearchCriteria;
import co.dazma.api.publications.services.usecases.models.PublicationSummaryPage;
import co.dazma.api.shared.domain.Municipality;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ListPublicationsServiceTest {

    private static final Duration DOWNLOAD_VALIDITY = Duration.ofMinutes(10);

    private PublicationRepositoryPort publicationRepository;
    private PublicationPhotoStoragePort photoStorage;
    private ListPublicationsService service;

    @BeforeEach
    void setUp() {
        publicationRepository = mock(PublicationRepositoryPort.class);
        photoStorage = mock(PublicationPhotoStoragePort.class);
        PhotoDownloadParametersPort downloadParameters = mock(PhotoDownloadParametersPort.class);
        when(downloadParameters.downloadValidity()).thenReturn(DOWNLOAD_VALIDITY);
        when(photoStorage.authorizeDownload(any(), any())).thenAnswer(invocation ->
                URI.create("https://storage.example/" + invocation.getArgument(0, String.class))
        );
        service = new ListPublicationsService(publicationRepository, photoStorage, downloadParameters);
    }

    @Test
    void shouldAskTheRepositoryForTheRequestedMunicipalityAndPage() {
        Optional<Municipality> municipality = Optional.of(new Municipality("05615"));
        when(publicationRepository.findPublished(municipality, 2, 10))
                .thenReturn(new PublishedPublicationsSlice(List.of(), false));

        PublicationSummaryPage page = service.execute(new PublicationSearchCriteria(municipality, 2, 10));

        verify(publicationRepository).findPublished(municipality, 2, 10);
        assertEquals(2, page.page());
        assertEquals(10, page.size());
        assertTrue(page.items().isEmpty());
    }

    @Test
    void shouldUseTheFirstPhotoByPositionAsTheSignedCoverEvenIfTheListIsUnordered() {
        UUID coverId = UUID.randomUUID();
        Publication publication = publication(List.of(
                new PublicationPhoto(UUID.randomUUID(), "publications/photo-1.jpg", 1),
                new PublicationPhoto(coverId, "publications/photo-0.jpg", 0)
        ));
        when(publicationRepository.findPublished(Optional.empty(), 0, 20))
                .thenReturn(new PublishedPublicationsSlice(List.of(publication), true));

        PublicationSummaryPage page = service.execute(new PublicationSearchCriteria(Optional.empty(), 0, 20));

        assertEquals(1, page.items().size());
        assertSame(publication, page.items().get(0).publication());
        assertEquals(coverId, page.items().get(0).cover().photoId());
        assertEquals(URI.create("https://storage.example/publications/photo-0.jpg"), page.items().get(0).cover().url());
        assertTrue(page.hasNext());
        verify(photoStorage).authorizeDownload("publications/photo-0.jpg", DOWNLOAD_VALIDITY);
        verifyNoMoreInteractions(photoStorage);
    }

    @Test
    void shouldReportThereIsNoNextPageWhenTheRepositoryDoesNot() {
        when(publicationRepository.findPublished(Optional.empty(), 0, 20))
                .thenReturn(new PublishedPublicationsSlice(
                        List.of(publication(List.of(new PublicationPhoto(UUID.randomUUID(), "publications/photo-0.jpg", 0)))),
                        false
                ));

        PublicationSummaryPage page = service.execute(new PublicationSearchCriteria(Optional.empty(), 0, 20));

        assertFalse(page.hasNext());
    }

    private static Publication publication(List<PublicationPhoto> photos) {
        return Publication.restore(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Ana",
                new Name("Bicicleta"),
                PublicationType.EXCHANGE,
                new Description("Bicicleta de montaña en buen estado"),
                new Municipality("05615"),
                PublicationStatus.PUBLISHED,
                photos,
                Instant.parse("2026-10-20T20:00:00Z"),
                new Price(900_000)
        );
    }
}
