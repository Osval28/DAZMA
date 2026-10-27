package co.dazma.api.notifications.infrastructure.output.templates;

import co.dazma.api.notifications.domain.NotificationErrorCode;
import co.dazma.api.shared.domain.errors.TechnicalException;

public class NotificationTemplateNotFoundException extends TechnicalException {

    public NotificationTemplateNotFoundException(String templatePath, String notificationKey, Throwable cause) {
        super(NotificationErrorCode.TEMPLATE_NOT_FOUND, cause, templatePath, notificationKey);
    }
}
