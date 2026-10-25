package co.dazma.api.publications.services.usecases;

import co.dazma.api.publications.domain.InvalidPublicationException;
import co.dazma.api.publications.domain.Price;
import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.Name;
import co.dazma.api.publications.domain.PublicationPhotoNotAvailableException;
import co.dazma.api.publications.domain.PublicationStatus;
import co.dazma.api.publications.domain.PublicationType;
import co.dazma.api.publications.events.PublicationCreated;
import co.dazma.api.publications.services.input.dto.CreatePublicationInputDto;
import co.dazma.api.publications.services.input.dto.CreatePublicationOutputDto;
import co.dazma.api.publications.services.output.models.StoredPublicationPhoto;
import co.dazma.api.publications.services.output.ports.PublicationEventsPort;
import co.dazma.api.publications.services.output.ports.PublicationOwnerPort;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import co.dazma.api.publications.services.output.ports.PublicationRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CreatePublicationServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-20T20:00:00Z");
    private static final String EXTERNAL_ID = "firebase-123";

    private PublicationOwnerPort publicationOwners;
    private UUID ownerId;
    private PublicationRepositoryPort publicationRepository;
    private PublicationPhotoStoragePort photoStorage;
    private PublicationEventsPort publicationEvents;
    private RecordingTransaction transaction;
    private CreatePublicationService service;

    @BeforeEach
    void setUp() {
        publicationOwners = mock(PublicationOwnerPort.class);
        ownerId = UUID.randomUUID();
        when(publicationOwners.resolveOperatingOwner(EXTERNAL_ID)).thenReturn(ownerId);
        publicationRepository = mock(PublicationRepositoryPort.class);
        photoStorage = mock(PublicationPhotoStoragePort.class);
        publicationEvents = mock(PublicationEventsPort.class);
        transaction = new RecordingTransaction();
        service = new CreatePublicationService(
                publicationOwners,
                publicationRepository,
                photoStorage,
                publicationEvents,
                transaction,
                Clock.fixed(NOW, ZoneOffset.UTC)
        );
    }

    @Test
    void shouldCreatePublicationWithVerifiedPhotosInRequestedOrder() {
        UUID firstPhotoId = UUID.randomUUID();
        UUID secondPhotoId = UUID.randomUUID();
        when(photoStorage.promoteUploadedPhoto(ownerId, firstPhotoId))
                .thenReturn(Optional.of(storedPhoto(ownerId, firstPhotoId)));
        when(photoStorage.promoteUploadedPhoto(ownerId, secondPhotoId))
                .thenReturn(Optional.of(storedPhoto(ownerId, secondPhotoId)));
        when(publicationRepository.save(any(Publication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreatePublicationOutputDto result = service.create(input(
                List.of(firstPhotoId, secondPhotoId)
        ));

        ArgumentCaptor<Publication> saved = ArgumentCaptor.forClass(Publication.class);
        verify(publicationRepository).save(saved.capture());
        Publication publication = saved.getValue();
        assertEquals(ownerId, publication.getOwnerId());
        assertEquals(new Name("Bicicleta"), publication.getName());
        assertEquals(PublicationType.EXCHANGE, publication.getType());
        assertEquals(new Price(900_000), publication.getPrice());
        assertEquals(firstPhotoId, publication.getPhotos().get(0).getId());
        assertEquals(0, publication.getPhotos().get(0).getPosition());
        assertEquals(secondPhotoId, publication.getPhotos().get(1).getId());
        assertEquals(1, publication.getPhotos().get(1).getPosition());

        assertEquals(publication.getId(), result.id());
        assertEquals(PublicationStatus.PUBLISHED, result.status());
        assertEquals(NOW, result.createdAt());
    }

    @Test
    void shouldAnnounceTheCreatedPublicationInsideTheTransaction() {
        UUID photoId = UUID.randomUUID();
        when(photoStorage.promoteUploadedPhoto(ownerId, photoId))
                .thenReturn(Optional.of(storedPhoto(ownerId, photoId)));
        when(publicationRepository.save(any(Publication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        AtomicBoolean publishedInsideTransaction = new AtomicBoolean(false);
        doAnswer(invocation -> {
            publishedInsideTransaction.set(transaction.active);
            return null;
        }).when(publicationEvents).publish(any(PublicationCreated.class));

        CreatePublicationOutputDto result = service.create(input(List.of(photoId)));

        ArgumentCaptor<PublicationCreated> event = ArgumentCaptor.forClass(PublicationCreated.class);
        verify(publicationEvents).publish(event.capture());
        assertEquals(result.id(), event.getValue().publicationId());
        assertEquals(ownerId, event.getValue().ownerId());
        assertEquals(NOW, event.getValue().createdAt());
        assertTrue(publishedInsideTransaction.get());
    }

    @Test
    void shouldVerifyPhotosOutsideTheTransaction() {
        UUID photoId = UUID.randomUUID();
        AtomicBoolean verifiedInsideTransaction = new AtomicBoolean(true);
        when(photoStorage.promoteUploadedPhoto(ownerId, photoId)).thenAnswer(invocation -> {
            verifiedInsideTransaction.set(transaction.active);
            return Optional.of(storedPhoto(ownerId, photoId));
        });
        when(publicationRepository.save(any(Publication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.create(input(List.of(photoId)));

        assertFalse(verifiedInsideTransaction.get());
    }

    @Test
    void shouldDiscardPromotedPhotosWhenThePublicationCannotBeSaved() {
        UUID photoId = UUID.randomUUID();
        StoredPublicationPhoto stored = storedPhoto(ownerId, photoId);
        when(photoStorage.promoteUploadedPhoto(ownerId, photoId)).thenReturn(Optional.of(stored));
        IllegalStateException failure = new IllegalStateException("base de datos caída");
        when(publicationRepository.save(any(Publication.class))).thenThrow(failure);

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> service.create(input(List.of(photoId)))
        );

        assertSame(failure, thrown);
        verify(photoStorage).discardPhotos(List.of(stored.objectKey()));
        verifyNoInteractions(publicationEvents);
    }

    @Test
    void shouldKeepTheOriginalFailureWhenDiscardingPhotosAlsoFails() {
        UUID photoId = UUID.randomUUID();
        when(photoStorage.promoteUploadedPhoto(ownerId, photoId))
                .thenReturn(Optional.of(storedPhoto(ownerId, photoId)));
        IllegalStateException failure = new IllegalStateException("base de datos caída");
        IllegalStateException discardFailure = new IllegalStateException("almacenamiento caído");
        when(publicationRepository.save(any(Publication.class))).thenThrow(failure);
        doThrow(discardFailure).when(photoStorage).discardPhotos(any());

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> service.create(input(List.of(photoId)))
        );

        assertSame(failure, thrown);
        assertSame(discardFailure, thrown.getSuppressed()[0]);
    }

    @Test
    void shouldDiscardAlreadyPromotedPhotosWhenALaterPhotoIsMissing() {
        UUID firstPhotoId = UUID.randomUUID();
        UUID missingPhotoId = UUID.randomUUID();
        StoredPublicationPhoto first = storedPhoto(ownerId, firstPhotoId);
        when(photoStorage.promoteUploadedPhoto(ownerId, firstPhotoId)).thenReturn(Optional.of(first));
        when(photoStorage.promoteUploadedPhoto(ownerId, missingPhotoId)).thenReturn(Optional.empty());

        assertThrows(
                PublicationPhotoNotAvailableException.class,
                () -> service.create(input(List.of(firstPhotoId, missingPhotoId)))
        );

        verify(photoStorage).discardPhotos(List.of(first.objectKey()));
        verify(publicationRepository, never()).save(any());
    }

    @Test
    void shouldNotTouchStorageWhenAFieldIsInvalid() {
        CreatePublicationInputDto invalid = new CreatePublicationInputDto(
                EXTERNAL_ID,
                "   ",
                PublicationType.EXCHANGE,
                "Bicicleta de montaña en buen estado",
                "Rionegro",
                900_000,
                List.of(UUID.randomUUID())
        );

        assertThrows(InvalidPublicationException.class, () -> service.create(invalid));

        verifyNoInteractions(photoStorage, publicationRepository, publicationEvents);
    }

    @Test
    void shouldRejectPhotoThatWasNotUploadedForOwner() {
        UUID photoId = UUID.randomUUID();
        when(photoStorage.promoteUploadedPhoto(ownerId, photoId))
                .thenReturn(Optional.empty());

        assertThrows(
                PublicationPhotoNotAvailableException.class,
                () -> service.create(input(List.of(photoId)))
        );

        verify(publicationRepository, never()).save(any());
        verifyNoInteractions(publicationEvents);
    }

    @Test
    void shouldRejectEmptyPhotoListBeforeSaving() {
        assertThrows(
                InvalidPublicationException.class,
                () -> service.create(input(List.of()))
        );

        verifyNoInteractions(publicationRepository, photoStorage, publicationEvents);
    }

    @Test
    void shouldNotTouchStorageWhenTheOwnerCannotOperate() {
        IllegalStateException rejection = new IllegalStateException("cannot operate");
        when(publicationOwners.resolveOperatingOwner(EXTERNAL_ID)).thenThrow(rejection);

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> service.create(input(List.of(UUID.randomUUID())))
        );

        assertSame(rejection, thrown);
        verifyNoInteractions(photoStorage, publicationRepository, publicationEvents);
    }

    private CreatePublicationInputDto input(List<UUID> photoIds) {
        return new CreatePublicationInputDto(
                EXTERNAL_ID,
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
                "publications/" + ownerId + "/" + UUID.randomUUID(),
                "image/jpeg",
                200_000
        );
    }

    private static final class RecordingTransaction implements TransactionOperations {

        private boolean active;

        @Override
        public <T> T execute(TransactionCallback<T> action) {
            active = true;
            try {
                return action.doInTransaction(null);
            } finally {
                active = false;
            }
        }
    }
}
