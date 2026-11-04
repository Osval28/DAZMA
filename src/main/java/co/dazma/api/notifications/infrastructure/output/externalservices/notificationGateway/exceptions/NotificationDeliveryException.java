package co.dazma.api.notifications.infrastructure.output.externalservices.notificationGateway.exceptions;

import co.dazma.api.notifications.domain.enums.NotificationErrorCode;
import co.dazma.api.shared.domain.errors.TechnicalException;

public class NotificationDeliveryException extends TechnicalException {

    private final boolean retryable;

    public NotificationDeliveryException(Integer statusCode, boolean retryable, Throwable cause) {
        super(NotificationErrorCode.DELIVERY_FAILED, cause, statusCode);
        this.retryable = retryable;
    }

    public boolean isRetryable() {
        return retryable;
    }
}
