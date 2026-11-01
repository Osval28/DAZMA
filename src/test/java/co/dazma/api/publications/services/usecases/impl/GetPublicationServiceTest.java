package co.dazma.api.publications.services.usecases.impl;

import co.dazma.api.publications.domain.Description;
import co.dazma.api.publications.domain.Name;
import co.dazma.api.publications.domain.Price;
import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationNotFoundException;
import co.dazma.api.publications.domain.PublicationPhoto;
import co.dazma.api.publications.domain.PublicationStatus;
import co.dazma.api.publications.domain.PublicationType;
import co.dazma.api.publications.services.output.ports.PhotoDownloadParametersPort;
import co.dazma.api.publications.services.output.ports.PublicationCachePort;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import co.dazma.api.publications.services.output.ports.PublicationRepositoryPort;
import co.dazma.api.publications.services.usecases.models.PhotoAccess;
import co.dazma.api.publications.services.usecases.models.PublicationDetail;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GetPublicationServiceTest {

    private static final Instant CREATED_AT = Instant.parse("2026-10-20T20:00:00Z");
    private static final Duration DOWNLOAD_VALIDITY = Duration.ofMinutes(10);

    private PublicationCachePort publicationCache;
    private PublicationRepositoryPort publicationRepository;
    private PublicationPhotoStoragePort photoStorage;
    private GetPublicationService service;

    @BeforeEach
    void setUp() {
        publicationCache = mock(PublicationCachePort.class);
        publicationRepository = mock(PublicationRepositoryPort.class);
        photoStorage = mock(PublicationPhotoStoragePort.class);
        PhotoDownloadParametersPort downloadParameters = mock(PhotoDownloadParametersPort.class);
        when(downloadParameters.downloadValidity()).thenReturn(DOWNLOAD_VALIDITY);
        when(photoStorage.authorizeDownload(any(), any())).thenAnswer(invocation ->
                URI.create("https://storage.example/" + invocation.getArgument(0, String.class))
        );
        service = new GetPublicationService(
                publicationCache,
                publicationRepository,
                photoStorage,
                downloadParameters
        );
    }

    @Test
    void shouldReturnCachedPublicationWithoutQueryingTheRepository() {
        Publication publication = publication();
        when(publicationCache.findById(publication.getId())).thenReturn(Optional.of(publication));

        PublicationDetail detail = service.execute(publication.getId());

        assertSame(publication, detail.publication());
        verifyNoInteractions(publicationRepository);
        verify(publicationCache, never()).save(any());
    }

    @Test
    void shouldLoadFromRepositoryAndCacheItWhenTheCacheMisses() {
        Publication publication = publication();
        when(publicationCache.findById(publication.getId())).thenReturn(Optional.empty());
        when(publicationRepository.findById(publication.getId())).thenReturn(Optional.of(publication));

        PublicationDetail detail = service.execute(publication.getId());

        assertSame(publication, detail.publication());
        verify(publicationCache).save(publication);
    }

    @Test
    void shouldThrowNotFoundAndCacheNothingWhenThePublicationDoesNotExist() {
        UUID publicationId = UUID.randomUUID();
        when(publicationCache.findById(publicationId)).thenReturn(Optional.empty());
        when(publicationRepository.findById(publicationId)).thenReturn(Optional.empty());

        assertThrows(PublicationNotFoundException.class, () -> service.execute(publicationId));
        verify(publicationCache, never()).save(any());
    }

    @Test
    void shouldSignPhotoUrlsInOrder() {
        Publication publication = publication();
        when(publicationCache.findById(publication.getId())).thenReturn(Optional.of(publication));

        PublicationDetail detail = service.execute(publication.getId());

        assertEquals(
                List.of(
                        new PhotoAccess(
                                publication.getPhotos().get(0).getId(),
                                URI.create("https://storage.example/publications/photo-0.jpg"),
                                0
                        ),
                        new PhotoAccess(
                                publication.getPhotos().get(1).getId(),
                                URI.create("https://storage.example/publications/photo-1.jpg"),
                                1
                        )
                ),
                detail.photos()
        );
        verify(photoStorage).authorizeDownload("publications/photo-0.jpg", DOWNLOAD_VALIDITY);
        verify(photoStorage).authorizeDownload("publications/photo-1.jpg", DOWNLOAD_VALIDITY);
    }

    private static Publication publication() {
        return Publication.restore(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Ana",
                new Name("Bicicleta"),
                PublicationType.EXCHANGE,
                new Description("Bicicleta de montaña en buen estado"),
                new Municipality("05615"),
                PublicationStatus.PUBLISHED,
                List.of(
                        new PublicationPhoto(UUID.randomUUID(), "publications/photo-0.jpg", 0),
                        new PublicationPhoto(UUID.randomUUID(), "publications/photo-1.jpg", 1)
                ),
                CREATED_AT,
                new Price(900_000)
        );
    }
}
