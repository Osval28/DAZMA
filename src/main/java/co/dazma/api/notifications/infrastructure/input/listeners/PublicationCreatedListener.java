package co.dazma.api.notifications.infrastructure.input.listeners;

import co.dazma.api.notifications.domain.NotificationType;
import co.dazma.api.notifications.services.input.dto.SendEmailInputDto;
import co.dazma.api.notifications.services.input.ports.SendEmailUseCase;
import co.dazma.api.publications.events.PublicationCreated;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PublicationCreatedListener {

    private final SendEmailUseCase sendEmailUseCase;

    public PublicationCreatedListener(SendEmailUseCase sendEmailUseCase) {
        this.sendEmailUseCase = sendEmailUseCase;
    }

    @ApplicationModuleListener
    public void on(PublicationCreated event) {
        sendEmailUseCase.send(new SendEmailInputDto(
                NotificationType.PUBLICATION_CREATED,
                event.ownerId(),
                event.publicationId(),
                Map.of()
        ));
    }
}
