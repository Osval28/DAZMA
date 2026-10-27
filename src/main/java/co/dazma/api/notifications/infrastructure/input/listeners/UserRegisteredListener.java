package co.dazma.api.notifications.infrastructure.input.listeners;

import co.dazma.api.notifications.services.input.dto.RegisterRecipientInputDto;
import co.dazma.api.notifications.services.input.ports.RegisterRecipientInputPort;
import co.dazma.api.users.events.UserRegistered;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredListener {

    private final RegisterRecipientInputPort registerRecipientInputPort;

    public UserRegisteredListener(RegisterRecipientInputPort registerRecipientInputPort) {
        this.registerRecipientInputPort = registerRecipientInputPort;
    }

    @ApplicationModuleListener
    public void on(UserRegistered event) {
        registerRecipientInputPort.execute(new RegisterRecipientInputDto(event.userId(), event.email()));
    }
}
