package co.dazma.api.shared.infrastructure.parameters;

import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ParameterMessagesTest {

    private final ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.ROOT);

    @Test
    void shouldHaveAMessageForEveryParameterErrorCode() {
        for (ParameterErrorCode errorCode : ParameterErrorCode.values()) {
            assertTrue(
                    messages.containsKey(errorCode.messageKey()),
                    () -> "No existe un mensaje para la clave: " + errorCode.messageKey()
            );
        }
    }
}
