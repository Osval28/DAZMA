package co.dazma.api.notifications.infrastructure.output.users;

import co.dazma.api.notifications.services.output.ports.RecipientPort;
import co.dazma.api.users.services.input.ports.ResolveUserEmailUseCase;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UsersRecipientAdapter implements RecipientPort {

    private final ResolveUserEmailUseCase resolveUserEmailUseCase;

    public UsersRecipientAdapter(ResolveUserEmailUseCase resolveUserEmailUseCase) {
        this.resolveUserEmailUseCase = resolveUserEmailUseCase;
    }

    @Override
    public String emailOf(UUID userId) {
        return resolveUserEmailUseCase.resolveEmail(userId);
    }
}
