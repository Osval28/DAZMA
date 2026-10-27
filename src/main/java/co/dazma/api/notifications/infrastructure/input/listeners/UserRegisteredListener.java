package co.dazma.api.notifications.infrastructure.input.listeners;

import co.dazma.api.notifications.services.input.dto.RegisterRecipientInputDto;
import co.dazma.api.notifications.services.input.ports.RegisterRecipientUseCase;
import co.dazma.api.users.events.UserRegistered;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredListener {

    private final RegisterRecipientUseCase registerRecipientUseCase;

    public UserRegisteredListener(RegisterRecipientUseCase registerRecipientUseCase) {
        this.registerRecipientUseCase = registerRecipientUseCase;
    }

    @ApplicationModuleListener
    public void on(UserRegistered event) {
        registerRecipientUseCase.register(new RegisterRecipientInputDto(event.userId(), event.email()));
    }
}
