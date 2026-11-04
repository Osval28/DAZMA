package co.dazma.api.chats.services.usecases.impl;

import co.dazma.api.chats.domain.models.ChatParticipant;
import co.dazma.api.chats.domain.exceptions.ChatParticipantCannotOperateException;
import co.dazma.api.chats.domain.exceptions.ChatParticipantNotReadyException;
import co.dazma.api.chats.domain.enums.ChatParticipantState;
import co.dazma.api.chats.domain.models.ChatRoom;
import co.dazma.api.chats.services.output.ports.ChatParticipantRepositoryPort;
import co.dazma.api.chats.services.output.ports.ChatRoomRepositoryPort;
import co.dazma.api.chats.services.usecases.models.ChatRoomSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListChatRoomsServiceTest {

    private static final Instant CREATED_AT = Instant.parse("2026-11-01T12:00:00Z");

    private ChatParticipantRepositoryPort participantRepository;
    private ChatRoomRepositoryPort roomRepository;
    private ListChatRoomsService service;
    private ChatParticipant user;

    @BeforeEach
    void setUp() {
        participantRepository = mock(ChatParticipantRepositoryPort.class);
        roomRepository = mock(ChatRoomRepositoryPort.class);
        service = new ListChatRoomsService(
                new OperatingParticipantResolver(participantRepository),
                roomRepository,
                participantRepository
        );
        user = new ChatParticipant(UUID.randomUUID(), "firebase-123", "Ana", ChatParticipantState.ACTIVE);
        when(participantRepository.findByExternalId("firebase-123")).thenReturn(Optional.of(user));
    }

    @Test
    void shouldNameEachRoomAfterTheOtherParticipantWhateverTheRole() {
        ChatParticipant buyer = new ChatParticipant(UUID.randomUUID(), "firebase-buyer", "Luis", ChatParticipantState.ACTIVE);
        ChatParticipant seller = new ChatParticipant(UUID.randomUUID(), "firebase-seller", "Marta", ChatParticipantState.ACTIVE);
        ChatRoom asSeller = ChatRoom.open(UUID.randomUUID(), user.userId(), buyer.userId(), CREATED_AT);
        ChatRoom asBuyer = ChatRoom.open(UUID.randomUUID(), seller.userId(), user.userId(), CREATED_AT);
        when(roomRepository.findByParticipant(user.userId())).thenReturn(List.of(asSeller, asBuyer));
        when(participantRepository.findAllByUserIds(List.of(buyer.userId(), seller.userId())))
                .thenReturn(List.of(buyer, seller));

        assertEquals(
                List.of(
                        new ChatRoomSummary(asSeller.getId(), "Luis"),
                        new ChatRoomSummary(asBuyer.getId(), "Marta")
                ),
                service.execute("firebase-123")
        );
    }

    @Test
    void shouldAnswerAnEmptyListWhenTheUserHasNoRooms() {
        when(roomRepository.findByParticipant(user.userId())).thenReturn(List.of());
        when(participantRepository.findAllByUserIds(List.of())).thenReturn(List.of());

        assertTrue(service.execute("firebase-123").isEmpty());
    }

    @Test
    void shouldAskForTheCounterpartCopyJustOnceWhenTheyShareSeveralRooms() {
        ChatParticipant buyer = new ChatParticipant(UUID.randomUUID(), "firebase-buyer", "Luis", ChatParticipantState.ACTIVE);
        ChatRoom first = ChatRoom.open(UUID.randomUUID(), user.userId(), buyer.userId(), CREATED_AT);
        ChatRoom second = ChatRoom.open(UUID.randomUUID(), user.userId(), buyer.userId(), CREATED_AT);
        when(roomRepository.findByParticipant(user.userId())).thenReturn(List.of(first, second));
        when(participantRepository.findAllByUserIds(List.of(buyer.userId()))).thenReturn(List.of(buyer));

        assertEquals(2, service.execute("firebase-123").size());
    }

    @Test
    void shouldNotListRoomsWhenTheCounterpartCopyIsMissing() {
        ChatRoom room = ChatRoom.open(UUID.randomUUID(), user.userId(), UUID.randomUUID(), CREATED_AT);
        when(roomRepository.findByParticipant(user.userId())).thenReturn(List.of(room));
        when(participantRepository.findAllByUserIds(any())).thenReturn(List.of());

        assertThrows(ChatParticipantNotReadyException.class, () -> service.execute("firebase-123"));
    }

    @Test
    void shouldNotListRoomsOfASuspendedUser() {
        when(participantRepository.findByExternalId("firebase-123")).thenReturn(Optional.of(
                new ChatParticipant(user.userId(), "firebase-123", "Ana", ChatParticipantState.SUSPENDED)
        ));

        assertThrows(ChatParticipantCannotOperateException.class, () -> service.execute("firebase-123"));
        verify(roomRepository, never()).findByParticipant(any());
    }
}
