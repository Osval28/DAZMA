package co.dazma.api.notifications.services.input.adapters;

import co.dazma.api.notifications.domain.EmailNotification;
import co.dazma.api.notifications.domain.NotificationType;
import co.dazma.api.notifications.services.input.adapters.mappers.EmailNotificationMapper;
import co.dazma.api.notifications.services.input.dto.SendEmailInputDto;
import co.dazma.api.notifications.services.usecases.contract.SendEmailUseCase;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SendEmailInputAdapterTest {

    @Test
    void shouldDelegateTheNotificationAsDomainToTheUseCase() {
        SendEmailUseCase useCase = mock(SendEmailUseCase.class);
        SendEmailInputAdapter adapter = new SendEmailInputAdapter(useCase, new EmailNotificationMapper());
        UUID userId = UUID.randomUUID();
        UUID publicationId = UUID.randomUUID();
        Map<String, String> data = Map.of("publicationName", "Bicicleta");

        adapter.execute(new SendEmailInputDto(NotificationType.PUBLICATION_CREATED, userId, publicationId, data));

        verify(useCase).execute(new EmailNotification(NotificationType.PUBLICATION_CREATED, userId, publicationId, data));
    }
}
