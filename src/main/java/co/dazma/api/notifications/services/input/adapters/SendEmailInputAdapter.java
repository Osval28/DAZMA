package co.dazma.api.notifications.services.input.adapters;

import co.dazma.api.notifications.services.input.adapters.mappers.EmailNotificationMapper;
import co.dazma.api.notifications.services.input.dto.SendEmailInputDto;
import co.dazma.api.notifications.services.input.ports.SendEmailInputPort;
import co.dazma.api.notifications.services.usecases.contract.SendEmailUseCase;
import org.springframework.stereotype.Service;

@Service
public class SendEmailInputAdapter implements SendEmailInputPort {

    private final SendEmailUseCase useCase;
    private final EmailNotificationMapper mapper;

    public SendEmailInputAdapter(SendEmailUseCase useCase, EmailNotificationMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @Override
    public void execute(SendEmailInputDto input) {
        useCase.execute(mapper.toDomain(input));
    }
}
