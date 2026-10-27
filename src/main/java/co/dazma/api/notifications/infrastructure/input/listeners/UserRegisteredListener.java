package co.dazma.api.notifications.infrastructure.input.listeners;

import co.dazma.api.notifications.domain.NotificationType;
import co.dazma.api.notifications.services.input.dto.RegisterRecipientInputDto;
import co.dazma.api.notifications.services.input.dto.SendEmailInputDto;
import co.dazma.api.notifications.services.input.ports.RegisterRecipientInputPort;
import co.dazma.api.notifications.services.input.ports.SendEmailInputPort;
import co.dazma.api.users.events.UserRegistered;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserRegisteredListener {

    private final RegisterRecipientInputPort registerRecipientInputPort;
    private final SendEmailInputPort sendEmailInputPort;

    public UserRegisteredListener(
            RegisterRecipientInputPort registerRecipientInputPort,
            SendEmailInputPort sendEmailInputPort
    ) {
        this.registerRecipientInputPort = registerRecipientInputPort;
        this.sendEmailInputPort = sendEmailInputPort;
    }

    @ApplicationModuleListener
    public void on(UserRegistered event) {
        registerRecipientInputPort.execute(new RegisterRecipientInputDto(event.userId(), event.email()));
        sendEmailInputPort.execute(new SendEmailInputDto(
                NotificationType.ACCOUNT_CREATED,
                event.userId(),
                event.userId(),
                Map.of()
        ));
    }
}
