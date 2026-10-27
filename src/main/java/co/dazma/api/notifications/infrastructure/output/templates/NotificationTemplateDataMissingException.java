package co.dazma.api.notifications.infrastructure.output.templates;

import co.dazma.api.notifications.domain.NotificationErrorCode;
import co.dazma.api.shared.domain.errors.TechnicalException;

public class NotificationTemplateDataMissingException extends TechnicalException {

    public NotificationTemplateDataMissingException(String notificationKey) {
        super(NotificationErrorCode.TEMPLATE_DATA_MISSING, null, notificationKey);
    }
}
