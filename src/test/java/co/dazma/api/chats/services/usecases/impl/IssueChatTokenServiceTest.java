package co.dazma.api.chats.services.usecases.impl;

import co.dazma.api.chats.domain.models.ChatParticipant;
import co.dazma.api.chats.domain.exceptions.ChatParticipantCannotOperateException;
import co.dazma.api.chats.domain.enums.ChatParticipantState;
import co.dazma.api.chats.domain.models.ChatRoom;
import co.dazma.api.chats.domain.exceptions.ChatRoomNotFoundException;
import co.dazma.api.chats.services.output.models.ChatTokenRequest;
import co.dazma.api.chats.services.output.ports.ChatParticipantRepositoryPort;
import co.dazma.api.chats.services.output.ports.ChatRoomRepositoryPort;
import co.dazma.api.chats.services.output.ports.ChatTokenIssuerPort;
import co.dazma.api.chats.services.usecases.models.IssueChatTokenCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IssueChatTokenServiceTest {

    private ChatParticipantRepositoryPort participantRepository;
    private ChatRoomRepositoryPort roomRepository;
    private ChatTokenIssuerPort tokenIssuer;
    private IssueChatTokenService service;
    private ChatParticipant participant;
    private ChatRoom room;

    @BeforeEach
    void setUp() {
        participantRepository = mock(ChatParticipantRepositoryPort.class);
        roomRepository = mock(ChatRoomRepositoryPort.class);
        tokenIssuer = mock(ChatTokenIssuerPort.class);
        service = new IssueChatTokenService(
                new OperatingParticipantResolver(participantRepository),
                roomRepository,
                tokenIssuer
        );
        participant = new ChatParticipant(UUID.randomUUID(), "firebase-123", "Ana", ChatParticipantState.ACTIVE);
        room = ChatRoom.open(UUID.randomUUID(), UUID.randomUUID(), participant.userId(), Instant.parse("2026-11-01T12:00:00Z"));
        when(participantRepository.findByExternalId("firebase-123")).thenReturn(Optional.of(participant));
        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
    }

    @Test
    void shouldIssueATokenToAMemberOfTheRoom() {
        ChatTokenRequest tokenRequest = new ChatTokenRequest("key", 3_600_000, 1L, "{}", "client", "nonce", "mac");
        when(tokenIssuer.issue(room, participant)).thenReturn(tokenRequest);

        assertEquals(tokenRequest, service.execute(new IssueChatTokenCommand("firebase-123", room.getId())));
    }

    @Test
    void shouldAnswerAsIfTheRoomDidNotExistToSomeoneWhoIsNotAMember() {
        ChatRoom foreignRoom = ChatRoom.open(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), Instant.parse("2026-11-01T12:00:00Z"));
        when(roomRepository.findById(foreignRoom.getId())).thenReturn(Optional.of(foreignRoom));

        assertThrows(
                ChatRoomNotFoundException.class,
                () -> service.execute(new IssueChatTokenCommand("firebase-123", foreignRoom.getId()))
        );
        verify(tokenIssuer, never()).issue(any(ChatRoom.class), any(ChatParticipant.class));
    }

    @Test
    void shouldRejectARoomThatDoesNotExist() {
        UUID unknownRoom = UUID.randomUUID();
        when(roomRepository.findById(unknownRoom)).thenReturn(Optional.empty());

        assertThrows(
                ChatRoomNotFoundException.class,
                () -> service.execute(new IssueChatTokenCommand("firebase-123", unknownRoom))
        );
    }

    @Test
    void shouldNotIssueTokensToASuspendedUser() {
        when(participantRepository.findByExternalId("firebase-123")).thenReturn(Optional.of(
                new ChatParticipant(participant.userId(), "firebase-123", "Ana", ChatParticipantState.SUSPENDED)
        ));

        assertThrows(
                ChatParticipantCannotOperateException.class,
                () -> service.execute(new IssueChatTokenCommand("firebase-123", room.getId()))
        );
        verify(tokenIssuer, never()).issue(any(ChatRoom.class), any(ChatParticipant.class));
    }
}
