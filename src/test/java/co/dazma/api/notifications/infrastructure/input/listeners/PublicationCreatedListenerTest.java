package co.dazma.api.notifications.infrastructure.input.listeners;

import co.dazma.api.notifications.domain.NotificationType;
import co.dazma.api.notifications.services.input.dto.SendEmailInputDto;
import co.dazma.api.notifications.services.input.ports.SendEmailUseCase;
import co.dazma.api.publications.events.PublicationCreated;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PublicationCreatedListenerTest {

    @Test
    void shouldNotifyTheOwnerWhenAPublicationIsCreated() {
        SendEmailUseCase sendEmailUseCase = mock(SendEmailUseCase.class);
        PublicationCreatedListener listener = new PublicationCreatedListener(sendEmailUseCase);
        UUID publicationId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        listener.on(new PublicationCreated(publicationId, ownerId, Instant.parse("2026-10-26T12:00:00Z")));

        verify(sendEmailUseCase).send(new SendEmailInputDto(
                NotificationType.PUBLICATION_CREATED,
                ownerId,
                publicationId,
                Map.of()
        ));
    }
}
