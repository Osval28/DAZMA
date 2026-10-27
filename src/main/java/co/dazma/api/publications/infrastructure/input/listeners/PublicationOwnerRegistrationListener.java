package co.dazma.api.publications.infrastructure.input.listeners;

import co.dazma.api.publications.services.input.dto.RegisterPublicationOwnerInputDto;
import co.dazma.api.publications.services.input.ports.RegisterPublicationOwnerInputPort;
import co.dazma.api.users.events.UserRegistered;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class PublicationOwnerRegistrationListener {

    private final RegisterPublicationOwnerInputPort registerOwnerInputPort;

    public PublicationOwnerRegistrationListener(RegisterPublicationOwnerInputPort registerOwnerInputPort) {
        this.registerOwnerInputPort = registerOwnerInputPort;
    }

    @ApplicationModuleListener
    public void on(UserRegistered event) {
        registerOwnerInputPort.execute(new RegisterPublicationOwnerInputDto(
                event.userId(),
                event.externalId(),
                event.state()
        ));
    }
}
