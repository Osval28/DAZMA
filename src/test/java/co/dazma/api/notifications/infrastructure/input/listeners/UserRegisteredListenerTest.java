package co.dazma.api.notifications.infrastructure.input.listeners;

import co.dazma.api.notifications.services.input.dto.RegisterRecipientInputDto;
import co.dazma.api.notifications.services.input.ports.RegisterRecipientInputPort;
import co.dazma.api.users.events.UserRegistered;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UserRegisteredListenerTest {

    @Test
    void shouldRegisterTheRecipientWhenAUserIsRegistered() {
        RegisterRecipientInputPort registerRecipientInputPort = mock(RegisterRecipientInputPort.class);
        UserRegisteredListener listener = new UserRegisteredListener(registerRecipientInputPort);
        UUID userId = UUID.randomUUID();

        listener.on(new UserRegistered(userId, "firebase-123", "ana@dazma.store", "ACTIVE"));

        verify(registerRecipientInputPort).execute(new RegisterRecipientInputDto(userId, "ana@dazma.store"));
    }
}
