package co.dazma.api.notifications.services.output.ports;

public class NotificationDeliveryException extends RuntimeException {

    private final boolean retryable;

    public NotificationDeliveryException(String message, boolean retryable, Throwable cause) {
        super(message, cause);
        this.retryable = retryable;
    }

    public boolean isRetryable() {
        return retryable;
    }

}
