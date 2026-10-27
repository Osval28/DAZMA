package co.dazma.api.security.services.input.adapters;

import co.dazma.api.security.services.input.dto.CloseSessionInputDto;
import co.dazma.api.security.services.input.ports.CloseSessionInputPort;
import co.dazma.api.security.services.usecases.contract.CloseSessionUseCase;

public class CloseSessionInputAdapter implements CloseSessionInputPort {

    private final CloseSessionUseCase useCase;

    public CloseSessionInputAdapter(CloseSessionUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public void execute(CloseSessionInputDto input) {
        useCase.execute(input.sessionToken());
    }
}
