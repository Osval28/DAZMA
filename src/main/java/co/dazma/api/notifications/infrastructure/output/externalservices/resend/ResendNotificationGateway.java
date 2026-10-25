package co.dazma.api.notifications.infrastructure.output.externalservices.resend;

import co.dazma.api.notifications.domain.EmailMessage;
import co.dazma.api.notifications.infrastructure.configuration.ResendProperties;
import co.dazma.api.notifications.services.output.ports.NotificationDeliveryException;
import co.dazma.api.notifications.services.output.ports.NotificationGateway;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.core.net.RequestOptions;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ResendNotificationGateway implements NotificationGateway {

    private static final int TOO_MANY_REQUESTS = 429;
    private static final int FIRST_SERVER_ERROR = 500;

    private final Resend resend;
    private final String sender;

    public ResendNotificationGateway(Resend resend, ResendProperties properties) {
        this.resend = resend;
        this.sender = properties.sender();
    }

    @Override
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
            resend.emails().send(email, options);
        } catch (ResendException exception) {
            throw new NotificationDeliveryException(
                    "Resend no pudo enviar el correo (estado HTTP: " + exception.getStatusCode() + ")",
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
