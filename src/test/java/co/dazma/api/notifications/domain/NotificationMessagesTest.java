package co.dazma.api.notifications.domain;

import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationMessagesTest {

    private final ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.ROOT);

    @Test
    void shouldHaveAMessageForEveryNotificationErrorCode() {
        for (NotificationErrorCode errorCode : NotificationErrorCode.values()) {
            assertTrue(
                    messages.containsKey(errorCode.messageKey()),
                    () -> "No existe un mensaje para la clave: " + errorCode.messageKey()
            );
        }
    }
}
