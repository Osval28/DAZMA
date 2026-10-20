package co.dazma.api.users.domain;

import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserMessagesTest {

    private final ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.ROOT);

    @Test
    void shouldHaveAMessageForEveryUserErrorCode() {
        for (UserErrorCode errorCode : UserErrorCode.values()) {
            assertTrue(
                    messages.containsKey(errorCode.messageKey()),
                    () -> "No existe un mensaje para la clave: " + errorCode.messageKey()
            );
        }
    }

    @Test
    void shouldInsertTheFieldNameIntoTheMissingFieldMessage() {
        String message = MessageFormat.format(
                messages.getString(UserErrorCode.REQUIRED_FIELD_MISSING.messageKey()),
                "phone"
        );

        assertEquals("El campo phone es obligatorio.", message);
    }
}
