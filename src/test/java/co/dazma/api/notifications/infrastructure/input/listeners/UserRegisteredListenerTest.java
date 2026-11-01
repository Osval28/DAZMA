package co.dazma.api.notifications.infrastructure.input.listeners;

import co.dazma.api.notifications.domain.NotificationType;
import co.dazma.api.notifications.services.input.dto.RegisterRecipientInputDto;
import co.dazma.api.notifications.services.input.dto.SendEmailInputDto;
import co.dazma.api.notifications.services.input.ports.RegisterRecipientInputPort;
import co.dazma.api.notifications.services.input.ports.SendEmailInputPort;
import co.dazma.api.users.events.UserRegistered;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

class UserRegisteredListenerTest {

    @Test
    void shouldRegisterTheRecipientBeforeSendingTheWelcomeEmail() {
        RegisterRecipientInputPort registerRecipientInputPort = mock(RegisterRecipientInputPort.class);
        SendEmailInputPort sendEmailInputPort = mock(SendEmailInputPort.class);
        UserRegisteredListener listener = new UserRegisteredListener(registerRecipientInputPort, sendEmailInputPort);
        UUID userId = UUID.randomUUID();

        listener.on(new UserRegistered(userId, "firebase-123", "Ana", "ana@dazma.store", "ACTIVE"));

        InOrder order = inOrder(registerRecipientInputPort, sendEmailInputPort);
        order.verify(registerRecipientInputPort).execute(new RegisterRecipientInputDto(userId, "ana@dazma.store"));
        order.verify(sendEmailInputPort).execute(new SendEmailInputDto(
                NotificationType.ACCOUNT_CREATED,
                userId,
                userId,
                Map.of()
        ));
    }
}
