package co.dazma.api.notifications.services.input.ports;

import co.dazma.api.notifications.domain.EmailMessage;

public interface SendEmailUseCase {

    void send(EmailMessage emailMessage);


}
