package co.dazma.api.notifications.services.input.ports;

import co.dazma.api.notifications.services.input.dto.SendEmailInputDto;

public interface SendEmailUseCase {

    void send(SendEmailInputDto input);
}
