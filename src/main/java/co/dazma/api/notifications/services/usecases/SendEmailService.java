package co.dazma.api.notifications.services.usecases;

import co.dazma.api.notifications.domain.EmailMessage;
import co.dazma.api.notifications.domain.Recipient;
import co.dazma.api.notifications.domain.RecipientNotFoundException;
import co.dazma.api.notifications.services.input.dto.SendEmailInputDto;
import co.dazma.api.notifications.services.input.ports.SendEmailUseCase;
import co.dazma.api.notifications.services.output.models.NotificationContent;
import co.dazma.api.notifications.services.output.ports.NotificationGateway;
import co.dazma.api.notifications.services.output.ports.NotificationTemplateCatalog;
import co.dazma.api.notifications.services.output.ports.RecipientRepositoryPort;
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
    public void send(SendEmailInputDto input) {
        Recipient recipient = recipientRepository.findByUserId(input.recipientUserId())
                .orElseThrow(() -> new RecipientNotFoundException(input.recipientUserId()));
        NotificationContent content = templateCatalog.render(input.type(), input.data());

        notificationGateway.send(new EmailMessage(
                recipient.email(),
                content.subject(),
                content.body(),
                input.type().key() + "/" + input.referenceId()
        ));
    }
}
