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

    @Test
    void shouldCreateAValidEmailMessage() {
        EmailMessage message = new EmailMessage(RECIPIENT, SUBJECT, BODY);

        assertEquals(RECIPIENT, message.to());
        assertEquals(SUBJECT, message.subject());
        assertEquals(BODY, message.body());
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

    private static void assertError(
            NotificationErrorCode expected,
            String recipient,
            String subject,
            String body
    ) {
        InvalidEmailMessageException exception = assertThrows(
                InvalidEmailMessageException.class,
                () -> new EmailMessage(recipient, subject, body)
        );
        assertEquals(expected, exception.errorCode());
    }
}
