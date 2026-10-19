package co.dazma.api.publications.domain;

import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PublicationMessagesTest {

    private final ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.ROOT);

    @Test
    void shouldHaveAMessageForEveryPublicationErrorCode() {
        for (PublicationErrorCode errorCode : PublicationErrorCode.values()) {
            assertTrue(
                    messages.containsKey(errorCode.messageKey()),
                    () -> "No existe un mensaje para la clave: " + errorCode.messageKey()
            );
        }
    }

    @Test
    void shouldInsertThePublicationIdIntoTheNotFoundMessage() {
        UUID publicationId = UUID.randomUUID();

        String message = MessageFormat.format(
                messages.getString(PublicationErrorCode.NOT_FOUND.messageKey()),
                publicationId
        );

        assertEquals(
                "No existe una publicación disponible con el identificador " + publicationId + ".",
                message
        );
    }
}
