package co.dazma.api.notifications.infrastructure.output.templates;

import co.dazma.api.notifications.domain.NotificationType;
import co.dazma.api.notifications.services.output.models.NotificationContent;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClasspathNotificationTemplateCatalogTest {

    private final ClasspathNotificationTemplateCatalog catalog = new ClasspathNotificationTemplateCatalog();

    @Test
    void shouldLoadATemplateForEveryNotificationType() {
        for (NotificationType type : NotificationType.values()) {
            NotificationContent content = catalog.render(type, Map.of());

            assertFalse(content.subject().isBlank());
            assertFalse(content.body().isBlank());
            assertFalse(content.body().contains("{{"));
        }
    }

    @Test
    void shouldRenderThePublicationCreatedTemplateAsUtf8Html() {
        NotificationContent content = catalog.render(NotificationType.PUBLICATION_CREATED, Map.of());

        assertEquals("Tu publicación ya está en DAZMA", content.subject());
        assertTrue(content.body().contains("<meta charset=\"UTF-8\">"));
        assertTrue(content.body().contains("¡Tu publicación ya está disponible!"));
    }
}
