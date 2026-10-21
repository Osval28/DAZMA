package co.dazma.api.publications.services.usecases;

import co.dazma.api.publications.domain.InvalidPublicationException;
import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationPhotoNotAvailableException;
import co.dazma.api.publications.domain.PublicationStatus;
import co.dazma.api.publications.domain.PublicationType;
import co.dazma.api.publications.services.input.models.CreatePublicationCommand;
import co.dazma.api.publications.services.output.models.StoredPublicationPhoto;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import co.dazma.api.publications.services.output.ports.PublicationRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CreatePublicationServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-20T20:00:00Z");

    private PublicationRepositoryPort publicationRepository;
    private PublicationPhotoStoragePort photoStorage;
    private CreatePublicationService service;

    @BeforeEach
    void setUp() {
        publicationRepository = mock(PublicationRepositoryPort.class);
        photoStorage = mock(PublicationPhotoStoragePort.class);
        service = new CreatePublicationService(
                publicationRepository,
                photoStorage,
                Clock.fixed(NOW, ZoneOffset.UTC)
        );
    }

    @Test
    void shouldCreatePublicationWithVerifiedPhotosInRequestedOrder() {
        UUID ownerId = UUID.randomUUID();
        UUID firstPhotoId = UUID.randomUUID();
        UUID secondPhotoId = UUID.randomUUID();
        when(photoStorage.findUploadedPhoto(ownerId, firstPhotoId))
                .thenReturn(Optional.of(storedPhoto(ownerId, firstPhotoId)));
        when(photoStorage.findUploadedPhoto(ownerId, secondPhotoId))
                .thenReturn(Optional.of(storedPhoto(ownerId, secondPhotoId)));
        when(publicationRepository.save(any(Publication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Publication result = service.create(command(
                ownerId,
                List.of(firstPhotoId, secondPhotoId)
        ));

        assertEquals(ownerId, result.getOwnerId());
        assertEquals("Bicicleta", result.getName());
        assertEquals(PublicationType.EXCHANGE, result.getType());
        assertEquals(PublicationStatus.PUBLISHED, result.getStatus());
        assertEquals(900_000, result.getPrice());
        assertEquals(NOW, result.getCreatedAt());
        assertEquals(firstPhotoId, result.getPhotos().get(0).getId());
        assertEquals(0, result.getPhotos().get(0).getPosition());
        assertEquals(secondPhotoId, result.getPhotos().get(1).getId());
        assertEquals(1, result.getPhotos().get(1).getPosition());
        verify(publicationRepository).save(result);
    }

    @Test
    void shouldRejectPhotoThatWasNotUploadedForOwner() {
        UUID ownerId = UUID.randomUUID();
        UUID photoId = UUID.randomUUID();
        when(photoStorage.findUploadedPhoto(ownerId, photoId))
                .thenReturn(Optional.empty());

        assertThrows(
                PublicationPhotoNotAvailableException.class,
                () -> service.create(command(ownerId, List.of(photoId)))
        );

        verify(publicationRepository, never()).save(any());
    }

    @Test
    void shouldRejectMissingCommandOwnerOrPhotosBeforeSaving() {
        assertThrows(InvalidPublicationException.class, () -> service.create(null));
        assertThrows(
                InvalidPublicationException.class,
                () -> service.create(command(null, List.of(UUID.randomUUID())))
        );
        assertThrows(
                InvalidPublicationException.class,
                () -> service.create(command(UUID.randomUUID(), List.of()))
        );

        verifyNoInteractions(publicationRepository, photoStorage);
    }

    private CreatePublicationCommand command(UUID ownerId, List<UUID> photoIds) {
        return new CreatePublicationCommand(
                ownerId,
                "Bicicleta",
                PublicationType.EXCHANGE,
                "Bicicleta de montaña en buen estado",
                "Rionegro",
                900_000,
                photoIds
        );
    }

    private StoredPublicationPhoto storedPhoto(UUID ownerId, UUID photoId) {
        return new StoredPublicationPhoto(
                photoId,
                "publications/" + ownerId + "/" + photoId,
                "image/jpeg",
                200_000
        );
    }
}
