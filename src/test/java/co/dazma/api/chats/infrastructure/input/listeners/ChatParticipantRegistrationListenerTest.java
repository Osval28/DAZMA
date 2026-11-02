package co.dazma.api.chats.infrastructure.input.listeners;

import co.dazma.api.chats.services.input.dto.RegisterChatParticipantInputDto;
import co.dazma.api.chats.services.input.ports.RegisterChatParticipantInputPort;
import co.dazma.api.users.events.UserRegistered;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ChatParticipantRegistrationListenerTest {

    @Test
    void shouldCopyTheRegisteredUserAsAChatParticipant() {
        RegisterChatParticipantInputPort inputPort = mock(RegisterChatParticipantInputPort.class);
        UUID userId = UUID.randomUUID();

        new ChatParticipantRegistrationListener(inputPort)
                .on(new UserRegistered(userId, "firebase-123", "Ana", "ana@dazma.store", "ACTIVE"));

        verify(inputPort).execute(new RegisterChatParticipantInputDto(userId, "firebase-123", "ACTIVE"));
    }
}
