package co.dazma.api.notifications.infrastructure.input.listeners;

import co.dazma.api.notifications.services.input.dto.RegisterRecipientInputDto;
import co.dazma.api.notifications.services.input.ports.RegisterRecipientUseCase;
import co.dazma.api.users.events.UserRegistered;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UserRegisteredListenerTest {

    @Test
    void shouldRegisterTheRecipientWhenAUserIsRegistered() {
        RegisterRecipientUseCase registerRecipientUseCase = mock(RegisterRecipientUseCase.class);
        UserRegisteredListener listener = new UserRegisteredListener(registerRecipientUseCase);
        UUID userId = UUID.randomUUID();

        listener.on(new UserRegistered(userId, "ana@dazma.store"));

        verify(registerRecipientUseCase).register(new RegisterRecipientInputDto(userId, "ana@dazma.store"));
    }
}
