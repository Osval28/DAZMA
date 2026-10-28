package co.dazma.api.notifications.infrastructure.output.externalservices.resend;

import co.dazma.api.notifications.domain.EmailMessage;
import co.dazma.api.notifications.infrastructure.configuration.ResendProperties;
import co.dazma.api.shared.infrastructure.observability.ExternalDependencyObservation;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.core.net.RequestOptions;
import com.resend.services.emails.Emails;
import com.resend.services.emails.model.CreateEmailOptions;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResendNotificationGatewayTest {

    private static final String SENDER = "DAZMA <no-reply@notificaciones.dazma.store>";
    private static final EmailMessage MESSAGE = new EmailMessage(
            "vendedor@dazma.store",
            "Tu publicación fue creada",
            "<p>Tu publicación ya está disponible.</p>",
            "publication-created/7f3c2a10-5b1e-4c2d-9a8f-1e2d3c4b5a69"
    );

    private Emails emails;
    private ResendNotificationGateway gateway;

    @BeforeEach
    void setUp() {
        Resend resend = mock(Resend.class);
        emails = mock(Emails.class);
        when(resend.emails()).thenReturn(emails);
        ResendProperties properties = new ResendProperties(
                "re_test",
                SENDER,
                Duration.ofSeconds(5),
                Duration.ofSeconds(10)
        );
        gateway = new ResendNotificationGateway(
                resend,
                properties,
                new ExternalDependencyObservation(ObservationRegistry.NOOP)
        );
    }

    @Test
    void shouldSendTheEmailWithTheConfiguredSenderAndIdempotencyKey() throws ResendException {
        gateway.send(MESSAGE);

        ArgumentCaptor<CreateEmailOptions> email = ArgumentCaptor.forClass(CreateEmailOptions.class);
        ArgumentCaptor<RequestOptions> options = ArgumentCaptor.forClass(RequestOptions.class);
        verify(emails).send(email.capture(), options.capture());
        assertEquals(SENDER, email.getValue().getFrom());
        assertEquals("vendedor@dazma.store", email.getValue().getTo().getFirst());
        assertEquals(MESSAGE.subject(), email.getValue().getSubject());
        assertEquals(MESSAGE.body(), email.getValue().getHtml());
        assertEquals(MESSAGE.idempotencyKey(), options.getValue().getIdempotencyKey());
    }

    @ParameterizedTest
    @ValueSource(ints = {429, 500, 503})
    void shouldMarkTransientFailuresAsRetryable(int statusCode) throws ResendException {
        ResendException failure = new ResendException("fallo", statusCode, "{}");
        when(emails.send(any(CreateEmailOptions.class), any(RequestOptions.class))).thenThrow(failure);

        NotificationDeliveryException exception = assertThrows(
                NotificationDeliveryException.class,
                () -> gateway.send(MESSAGE)
        );

        assertTrue(exception.isRetryable());
        assertSame(failure, exception.getCause());
    }

    @Test
    void shouldMarkNetworkFailuresWithoutStatusAsRetryable() throws ResendException {
        when(emails.send(any(CreateEmailOptions.class), any(RequestOptions.class)))
                .thenThrow(new ResendException("timeout"));

        NotificationDeliveryException exception = assertThrows(
                NotificationDeliveryException.class,
                () -> gateway.send(MESSAGE)
        );

        assertTrue(exception.isRetryable());
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 401, 403, 422})
    void shouldNotRetryPermanentFailures(int statusCode) throws ResendException {
        when(emails.send(any(CreateEmailOptions.class), any(RequestOptions.class)))
                .thenThrow(new ResendException("fallo", statusCode, "{}"));

        NotificationDeliveryException exception = assertThrows(
                NotificationDeliveryException.class,
                () -> gateway.send(MESSAGE)
        );

        assertFalse(exception.isRetryable());
    }
}
