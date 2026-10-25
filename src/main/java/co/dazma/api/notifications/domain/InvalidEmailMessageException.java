package co.dazma.api.notifications.domain;

import co.dazma.api.shared.domain.errors.DomainInvalidException;

public class InvalidEmailMessageException extends DomainInvalidException {

    public InvalidEmailMessageException(NotificationErrorCode notificationErrorCode, Object... arguments) {
        super(notificationErrorCode, arguments);
    }
}
