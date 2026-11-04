package co.dazma.api.chats.services.usecases.impl;

import co.dazma.api.chats.domain.models.ChatParticipant;
import co.dazma.api.chats.domain.exceptions.ChatParticipantCannotOperateException;
import co.dazma.api.chats.domain.exceptions.ChatParticipantNotReadyException;
import co.dazma.api.chats.domain.enums.ChatParticipantState;
import co.dazma.api.chats.services.output.ports.ChatParticipantRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OperatingParticipantResolverTest {

    private ChatParticipantRepositoryPort participantRepository;
    private OperatingParticipantResolver resolver;

    @BeforeEach
    void setUp() {
        participantRepository = mock(ChatParticipantRepositoryPort.class);
        resolver = new OperatingParticipantResolver(participantRepository);
    }

    @Test
    void shouldResolveAnActiveParticipant() {
        ChatParticipant participant = new ChatParticipant(UUID.randomUUID(), "firebase-123", "Ana", ChatParticipantState.ACTIVE);
        when(participantRepository.findByExternalId("firebase-123")).thenReturn(Optional.of(participant));

        assertEquals(participant, resolver.resolve("firebase-123"));
    }

    @Test
    void shouldRejectAParticipantThatIsNotInTheLocalCopyYet() {
        when(participantRepository.findByExternalId("firebase-123")).thenReturn(Optional.empty());

        assertThrows(ChatParticipantNotReadyException.class, () -> resolver.resolve("firebase-123"));
    }

    @Test
    void shouldRejectASuspendedParticipant() {
        when(participantRepository.findByExternalId("firebase-123")).thenReturn(Optional.of(
                new ChatParticipant(UUID.randomUUID(), "firebase-123", "Ana", ChatParticipantState.SUSPENDED)
        ));

        assertThrows(ChatParticipantCannotOperateException.class, () -> resolver.resolve("firebase-123"));
    }
}
