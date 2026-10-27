package co.dazma.api.notifications.services.usecases.impl;

import co.dazma.api.notifications.domain.EmailMessage;
import co.dazma.api.notifications.domain.EmailNotification;
import co.dazma.api.notifications.domain.Recipient;
import co.dazma.api.notifications.domain.RecipientNotFoundException;
import co.dazma.api.notifications.services.output.models.NotificationContent;
import co.dazma.api.notifications.services.output.ports.NotificationGateway;
import co.dazma.api.notifications.services.output.ports.NotificationTemplateCatalog;
import co.dazma.api.notifications.services.output.ports.RecipientRepositoryPort;
import co.dazma.api.notifications.services.usecases.contract.SendEmailUseCase;
import org.springframework.stereotype.Service;

@Service
public class SendEmailService implements SendEmailUseCase {

    private final RecipientRepositoryPort recipientRepository;
    private final NotificationTemplateCatalog templateCatalog;
    private final NotificationGateway notificationGateway;

    public SendEmailService(
            RecipientRepositoryPort recipientRepository,
            NotificationTemplateCatalog templateCatalog,
            NotificationGateway notificationGateway
    ) {
        this.recipientRepository = recipientRepository;
        this.templateCatalog = templateCatalog;
        this.notificationGateway = notificationGateway;
    }

    @Override
    public void execute(EmailNotification notification) {
        Recipient recipient = recipientRepository.findByUserId(notification.recipientUserId())
                .orElseThrow(() -> new RecipientNotFoundException(notification.recipientUserId()));
        NotificationContent content = templateCatalog.render(notification.type(), notification.data());

        notificationGateway.send(new EmailMessage(
                recipient.email(),
                content.subject(),
                content.body(),
                notification.idempotencyKey()
        ));
    }
}
