package co.dazma.api.users.services.input.ports;

import java.util.UUID;

public interface ResolveUserEmailUseCase {

    String resolveEmail(UUID userId);
}
