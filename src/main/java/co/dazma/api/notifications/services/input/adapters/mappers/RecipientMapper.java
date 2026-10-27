package co.dazma.api.notifications.services.input.adapters.mappers;

import co.dazma.api.notifications.domain.Recipient;
import co.dazma.api.notifications.services.input.dto.RegisterRecipientInputDto;
import co.dazma.api.shared.services.input.mappers.InputMapper;
import org.springframework.stereotype.Component;

@Component
public class RecipientMapper implements InputMapper<RegisterRecipientInputDto, Recipient> {

    @Override
    public Recipient toDomain(RegisterRecipientInputDto input) {
        return new Recipient(input.userId(), input.email());
    }
}
