package co.dazma.api.notifications.services.input.ports;

import co.dazma.api.notifications.services.input.dto.RegisterRecipientInputDto;

public interface RegisterRecipientUseCase {

    void register(RegisterRecipientInputDto input);
}
