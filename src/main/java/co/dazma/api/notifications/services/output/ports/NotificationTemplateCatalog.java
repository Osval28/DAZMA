package co.dazma.api.notifications.services.output.ports;

import co.dazma.api.notifications.domain.enums.NotificationType;
import co.dazma.api.notifications.services.output.models.NotificationContent;

import java.util.Map;

public interface NotificationTemplateCatalog {

    NotificationContent render(NotificationType type, Map<String, String> data);
}
