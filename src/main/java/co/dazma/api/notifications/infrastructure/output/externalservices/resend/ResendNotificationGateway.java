package co.dazma.api.notifications.infrastructure.output.externalservices.resend;

import co.dazma.api.notifications.domain.EmailMessage;
import co.dazma.api.notifications.infrastructure.configuration.ResendProperties;
import co.dazma.api.notifications.services.output.ports.NotificationGateway;
import co.dazma.api.shared.infrastructure.observability.ExternalDependencyObservation;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.core.net.RequestOptions;
import com.resend.services.emails.model.CreateEmailOptions;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ResendNotificationGateway implements NotificationGateway {

    private static final int TOO_MANY_REQUESTS = 429;
    private static final int FIRST_SERVER_ERROR = 500;

    private final Resend resend;
    private final String sender;
    private final ExternalDependencyObservation observations;

    public ResendNotificationGateway(
            Resend resend,
            ResendProperties properties,
            ExternalDependencyObservation observations
    ) {
        this.resend = resend;
        this.sender = properties.sender();
        this.observations = observations;
    }

    @Override
    @Retry(name = "resend")
    @CircuitBreaker(name = "resend")
    public void send(EmailMessage emailMessage) {
        CreateEmailOptions email = CreateEmailOptions.builder()
                .from(sender)
                .to(emailMessage.to())
                .subject(emailMessage.subject())
                .html(emailMessage.body())
                .headers(Map.of(
                        "Importance", "high",
                        "X-Priority", "1",
                        "X-MSMail-Priority", "High"
                ))
                .build();
        RequestOptions options = RequestOptions.builder()
                .setIdempotencyKey(emailMessage.idempotencyKey())
                .build();

        try {
            observations.observeChecked(
                    "resend",
                    "send_email",
                    () -> resend.emails().send(email, options)
            );
        } catch (ResendException exception) {
            throw new NotificationDeliveryException(
                    exception.getStatusCode(),
                    isRetryable(exception.getStatusCode()),
                    exception
            );
        }
    }

    private static boolean isRetryable(Integer statusCode) {
        return statusCode == null
                || statusCode == TOO_MANY_REQUESTS
                || statusCode >= FIRST_SERVER_ERROR;
    }
}
