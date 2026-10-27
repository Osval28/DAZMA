package co.dazma.api.notifications.services.usecases;

import co.dazma.api.notifications.domain.EmailMessage;
import co.dazma.api.notifications.domain.NotificationType;
import co.dazma.api.notifications.domain.Recipient;
import co.dazma.api.notifications.domain.RecipientNotFoundException;
import co.dazma.api.notifications.services.input.dto.SendEmailInputDto;
import co.dazma.api.notifications.services.output.models.NotificationContent;
import co.dazma.api.notifications.services.output.ports.NotificationGateway;
import co.dazma.api.notifications.services.output.ports.NotificationTemplateCatalog;
import co.dazma.api.notifications.services.output.ports.RecipientRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SendEmailServiceTest {

    private static final UUID USER_ID = UUID.fromString("0b6f3c2e-4d1a-4f7e-9c3b-2a1d5e6f7a8b");
    private static final UUID PUBLICATION_ID = UUID.fromString("7f3c2a10-5b1e-4c2d-9a8f-1e2d3c4b5a69");
    private static final Map<String, String> DATA = Map.of("publicationName", "Bicicleta");

    private RecipientRepositoryPort recipientRepository;
    private NotificationTemplateCatalog templateCatalog;
    private NotificationGateway notificationGateway;
    private SendEmailService service;

    @BeforeEach
    void setUp() {
        recipientRepository = mock(RecipientRepositoryPort.class);
        templateCatalog = mock(NotificationTemplateCatalog.class);
        notificationGateway = mock(NotificationGateway.class);
        service = new SendEmailService(recipientRepository, templateCatalog, notificationGateway);
    }

    @Test
    void shouldSendTheRenderedTemplateToTheRecipient() {
        when(recipientRepository.findByUserId(USER_ID))
                .thenReturn(Optional.of(new Recipient(USER_ID, "ana@dazma.store")));
        when(templateCatalog.render(NotificationType.PUBLICATION_CREATED, DATA))
                .thenReturn(new NotificationContent("Asunto", "<p>Cuerpo</p>"));

        service.send(new SendEmailInputDto(
                NotificationType.PUBLICATION_CREATED,
                USER_ID,
                PUBLICATION_ID,
                DATA
        ));

        verify(notificationGateway).send(new EmailMessage(
                "ana@dazma.store",
                "Asunto",
                "<p>Cuerpo</p>",
                "publication-created/" + PUBLICATION_ID
        ));
    }

    @Test
    void shouldFailWhenTheRecipientIsNotRegisteredYet() {
        when(recipientRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        SendEmailInputDto input = new SendEmailInputDto(
                NotificationType.PUBLICATION_CREATED,
                USER_ID,
                PUBLICATION_ID,
                DATA
        );

        RecipientNotFoundException exception = assertThrows(
                RecipientNotFoundException.class,
                () -> service.send(input)
        );

        assertEquals("NOTIFICATION_RECIPIENT_NOT_FOUND", exception.code());
        verifyNoInteractions(templateCatalog);
        verify(notificationGateway, never()).send(any(EmailMessage.class));
    }
}
