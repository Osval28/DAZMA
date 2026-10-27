package co.dazma.api.users.services.input.adapters;

import co.dazma.api.users.services.input.ports.ResolveUserIdInputPort;
import co.dazma.api.users.services.usecases.contract.ResolveUserIdUseCase;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ResolveUserIdInputAdapter implements ResolveUserIdInputPort {

    private final ResolveUserIdUseCase useCase;

    public ResolveUserIdInputAdapter(ResolveUserIdUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public UUID execute(String externalId) {
        return useCase.execute(externalId);
    }
}
