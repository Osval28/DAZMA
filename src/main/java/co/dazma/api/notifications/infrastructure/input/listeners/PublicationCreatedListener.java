package co.dazma.api.notifications.infrastructure.input.listeners;

import co.dazma.api.notifications.domain.NotificationType;
import co.dazma.api.notifications.infrastructure.configuration.EmailExecutorConfiguration;
import co.dazma.api.notifications.services.input.dto.SendEmailInputDto;
import co.dazma.api.notifications.services.input.ports.SendEmailInputPort;
import co.dazma.api.publications.events.PublicationCreated;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
public class PublicationCreatedListener {

    private final SendEmailInputPort sendEmailInputPort;

    public PublicationCreatedListener(SendEmailInputPort sendEmailInputPort) {
        this.sendEmailInputPort = sendEmailInputPort;
    }

    @Async(EmailExecutorConfiguration.EMAIL_EXECUTOR)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void on(PublicationCreated event) {
        sendEmailInputPort.execute(new SendEmailInputDto(
                NotificationType.PUBLICATION_CREATED,
                event.ownerId(),
                event.publicationId(),
                Map.of()
        ));
    }
}
