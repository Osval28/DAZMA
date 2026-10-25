package co.dazma.api.users.services.input.ports;

import java.util.UUID;

public interface ResolveUserIdUseCase {

    UUID resolveByExternalId(String externalId);
}
