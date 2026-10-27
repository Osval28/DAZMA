package co.dazma.api.notifications.services.input.adapters;

import co.dazma.api.notifications.services.input.adapters.mappers.RecipientMapper;
import co.dazma.api.notifications.services.input.dto.RegisterRecipientInputDto;
import co.dazma.api.notifications.services.input.ports.RegisterRecipientInputPort;
import co.dazma.api.notifications.services.usecases.contract.RegisterRecipientUseCase;
import org.springframework.stereotype.Service;

@Service
public class RegisterRecipientInputAdapter implements RegisterRecipientInputPort {

    private final RegisterRecipientUseCase useCase;
    private final RecipientMapper mapper;

    public RegisterRecipientInputAdapter(RegisterRecipientUseCase useCase, RecipientMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @Override
    public void execute(RegisterRecipientInputDto input) {
        useCase.execute(mapper.toDomain(input));
    }
}
