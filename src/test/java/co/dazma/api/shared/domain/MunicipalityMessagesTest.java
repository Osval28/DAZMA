package co.dazma.api.shared.domain;

import co.dazma.api.shared.domain.errors.MunicipalityErrorCode;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MunicipalityMessagesTest {

    private final ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.ROOT);

    @Test
    void shouldHaveAMessageForEveryMunicipalityErrorCode() {
        for (MunicipalityErrorCode errorCode : MunicipalityErrorCode.values()) {
            assertTrue(
                    messages.containsKey(errorCode.messageKey()),
                    () -> "No existe un mensaje para la clave: " + errorCode.messageKey()
            );
        }
    }
}
