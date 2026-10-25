package co.dazma.api.notifications.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailMessageTest {

    private static final String RECIPIENT = "vendedor@dazma.store";
    private static final String SUBJECT = "Tu publicación fue creada";
    private static final String BODY = "<p>Tu publicación ya está disponible.</p>";
    private static final String IDEMPOTENCY_KEY = "publication-created/7f3c2a10-5b1e-4c2d-9a8f-1e2d3c4b5a69";

    @Test
    void shouldCreateAValidEmailMessage() {
        EmailMessage message = new EmailMessage(RECIPIENT, SUBJECT, BODY, IDEMPOTENCY_KEY);

        assertEquals(RECIPIENT, message.to());
        assertEquals(SUBJECT, message.subject());
        assertEquals(BODY, message.body());
        assertEquals(IDEMPOTENCY_KEY, message.idempotencyKey());
    }

    @Test
    void shouldRejectABlankRecipient() {
        assertError(NotificationErrorCode.EMAIL_REQUIRED, " ", SUBJECT, BODY);
    }

    @ParameterizedTest
    @ValueSource(strings = {"vendedor", "vendedor@dazma", "vendedor @dazma.store", "@dazma.store"})
    void shouldRejectARecipientWithAnInvalidFormat(String recipient) {
        assertError(NotificationErrorCode.EMAIL_INVALID, recipient, SUBJECT, BODY);
    }

    @Test
    void shouldRejectABlankSubject() {
        assertError(NotificationErrorCode.SUBJECT_REQUIRED, RECIPIENT, " ", BODY);
    }

    @Test
    void shouldRejectABlankBody() {
        assertError(NotificationErrorCode.BODY_REQUIRED, RECIPIENT, SUBJECT, " ");
    }

    @Test
    void shouldRejectABlankIdempotencyKey() {
        assertError(NotificationErrorCode.IDEMPOTENCY_KEY_REQUIRED, RECIPIENT, SUBJECT, BODY, " ");
    }

    @Test
    void shouldRejectAnIdempotencyKeyLongerThanResendAllows() {
        assertError(NotificationErrorCode.IDEMPOTENCY_KEY_TOO_LONG, RECIPIENT, SUBJECT, BODY, "k".repeat(257));
    }

    @Test
    void shouldAcceptAnIdempotencyKeyAtTheMaximumLength() {
        EmailMessage message = new EmailMessage(RECIPIENT, SUBJECT, BODY, "k".repeat(256));

        assertEquals(256, message.idempotencyKey().length());
    }

    private static void assertError(
            NotificationErrorCode expected,
            String recipient,
            String subject,
            String body
    ) {
        assertError(expected, recipient, subject, body, IDEMPOTENCY_KEY);
    }

    private static void assertError(
            NotificationErrorCode expected,
            String recipient,
            String subject,
            String body,
            String idempotencyKey
    ) {
        InvalidEmailMessageException exception = assertThrows(
                InvalidEmailMessageException.class,
                () -> new EmailMessage(recipient, subject, body, idempotencyKey)
        );
        assertEquals(expected, exception.errorCode());
    }
}
