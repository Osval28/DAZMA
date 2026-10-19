package co.dazma.api.security.domain;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertFalse;

class SecurityMessagesTest {

    @Test
    void shouldDefineAMessageForEverySecurityErrorCode() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());

        for (SecurityErrorCode errorCode : SecurityErrorCode.values()) {
            String message = messageSource.getMessage(
                    errorCode.messageKey(),
                    new Object[0],
                    Locale.forLanguageTag("es")
            );

            assertFalse(message.isBlank());
        }
    }
}
