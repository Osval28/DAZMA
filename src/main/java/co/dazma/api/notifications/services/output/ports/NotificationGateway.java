package co.dazma.api.notifications.services.output.ports;

import co.dazma.api.notifications.domain.models.EmailMessage;

public interface NotificationGateway {

    void send(EmailMessage emailMessage);

}
