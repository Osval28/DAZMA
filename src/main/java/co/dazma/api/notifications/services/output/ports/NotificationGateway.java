package co.dazma.api.notifications.services.output.ports;

import co.dazma.api.notifications.domain.EmailMessage;

public interface NotificationGateway {

    void send(EmailMessage emailMessage);

}
