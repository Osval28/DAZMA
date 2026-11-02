package co.dazma.api.chats.services.usecases.impl;

import co.dazma.api.chats.domain.ChatEmailNotVerifiedException;
import co.dazma.api.chats.domain.ChatOffer;
import co.dazma.api.chats.domain.ChatOfferNotFoundException;
import co.dazma.api.chats.domain.ChatParticipant;
import co.dazma.api.chats.domain.ChatParticipantCannotOperateException;
import co.dazma.api.chats.domain.ChatParticipantState;
import co.dazma.api.chats.domain.ChatRoom;
import co.dazma.api.chats.domain.ChatRoomOpening;
import co.dazma.api.chats.domain.SelfChatNotAllowedException;
import co.dazma.api.chats.services.output.ports.ChatOfferRepositoryPort;
import co.dazma.api.chats.services.output.ports.ChatParticipantRepositoryPort;
import co.dazma.api.chats.services.output.ports.ChatRoomRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OpenChatRoomServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-01T12:00:00Z");

    private ChatParticipantRepositoryPort participantRepository;
    private ChatOfferRepositoryPort offerRepository;
    private ChatRoomRepositoryPort roomRepository;
    private OpenChatRoomService service;
    private UUID publicationId;
    private UUID ownerId;
    private UUID interestedId;

    @BeforeEach
    void setUp() {
        participantRepository = mock(ChatParticipantRepositoryPort.class);
        offerRepository = mock(ChatOfferRepositoryPort.class);
        roomRepository = mock(ChatRoomRepositoryPort.class);
        service = new OpenChatRoomService(
                new OperatingParticipantResolver(participantRepository),
                offerRepository,
                roomRepository,
                Clock.fixed(NOW, ZoneOffset.UTC)
        );
        publicationId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        interestedId = UUID.randomUUID();
        when(participantRepository.findByExternalId("firebase-123")).thenReturn(Optional.of(
                new ChatParticipant(interestedId, "firebase-123", "Ana", ChatParticipantState.ACTIVE)
        ));
        when(offerRepository.findByPublicationId(publicationId))
                .thenReturn(Optional.of(new ChatOffer(publicationId, ownerId)));
        when(roomRepository.findByPublicationAndInterested(publicationId, interestedId)).thenReturn(Optional.empty());
        when(roomRepository.save(any(ChatRoom.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldOpenANewRoomBetweenTheInterestedAndTheOwner() {
        ChatRoom room = service.execute(new ChatRoomOpening("firebase-123", true, publicationId));

        assertEquals(publicationId, room.getPublicationId());
        assertEquals(ownerId, room.getOwnerId());
        assertEquals(interestedId, room.getInterestedId());
        assertEquals(NOW, room.getCreatedAt());
        verify(roomRepository).save(room);
    }

    @Test
    void shouldReuseTheRoomThatAlreadyExists() {
        ChatRoom existing = ChatRoom.open(publicationId, ownerId, interestedId, NOW.minusSeconds(60));
        when(roomRepository.findByPublicationAndInterested(publicationId, interestedId))
                .thenReturn(Optional.of(existing));

        ChatRoom room = service.execute(new ChatRoomOpening("firebase-123", true, publicationId));

        assertEquals(existing, room);
        verify(roomRepository, never()).save(any(ChatRoom.class));
    }

    @Test
    void shouldRequireAVerifiedEmail() {
        assertThrows(
                ChatEmailNotVerifiedException.class,
                () -> service.execute(new ChatRoomOpening("firebase-123", false, publicationId))
        );

        verify(roomRepository, never()).save(any(ChatRoom.class));
    }

    @Test
    void shouldRejectAPublicationThatIsNotInTheLocalCopy() {
        UUID unknownPublication = UUID.randomUUID();
        when(offerRepository.findByPublicationId(unknownPublication)).thenReturn(Optional.empty());

        assertThrows(
                ChatOfferNotFoundException.class,
                () -> service.execute(new ChatRoomOpening("firebase-123", true, unknownPublication))
        );
    }

    @Test
    void shouldRejectASuspendedUser() {
        when(participantRepository.findByExternalId("firebase-123")).thenReturn(Optional.of(
                new ChatParticipant(interestedId, "firebase-123", "Ana", ChatParticipantState.SUSPENDED)
        ));

        assertThrows(
                ChatParticipantCannotOperateException.class,
                () -> service.execute(new ChatRoomOpening("firebase-123", true, publicationId))
        );
    }

    @Test
    void shouldRejectTheOwnerOfThePublication() {
        when(offerRepository.findByPublicationId(publicationId))
                .thenReturn(Optional.of(new ChatOffer(publicationId, interestedId)));

        assertThrows(
                SelfChatNotAllowedException.class,
                () -> service.execute(new ChatRoomOpening("firebase-123", true, publicationId))
        );
    }
}
