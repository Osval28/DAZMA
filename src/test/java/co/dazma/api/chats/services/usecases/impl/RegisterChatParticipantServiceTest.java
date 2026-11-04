package co.dazma.api.chats.services.usecases.impl;

import co.dazma.api.chats.domain.models.ChatParticipant;
import co.dazma.api.chats.domain.enums.ChatParticipantState;
import co.dazma.api.chats.services.output.ports.ChatParticipantRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RegisterChatParticipantServiceTest {

    @Test
    void shouldSaveTheParticipantInTheLocalCopy() {
        ChatParticipantRepositoryPort participantRepository = mock(ChatParticipantRepositoryPort.class);
        ChatParticipant participant = new ChatParticipant(UUID.randomUUID(), "firebase-123", "Ana", ChatParticipantState.ACTIVE);

        new RegisterChatParticipantService(participantRepository).execute(participant);

        verify(participantRepository).save(participant);
    }
}
