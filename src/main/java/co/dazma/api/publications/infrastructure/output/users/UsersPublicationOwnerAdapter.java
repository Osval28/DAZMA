package co.dazma.api.publications.infrastructure.output.users;

import co.dazma.api.publications.services.output.ports.PublicationOwnerPort;
import co.dazma.api.users.services.input.ports.ResolveUserIdInputPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UsersPublicationOwnerAdapter implements PublicationOwnerPort {

    private final ResolveUserIdInputPort resolveUserIdInputPort;

    public UsersPublicationOwnerAdapter(ResolveUserIdInputPort resolveUserIdInputPort) {
        this.resolveUserIdInputPort = resolveUserIdInputPort;
    }

    @Override
    public UUID resolveOperatingOwner(String externalId) {
        return resolveUserIdInputPort.execute(externalId);
    }
}
