package co.dazma.api.notifications.domain;

import co.dazma.api.shared.domain.errors.ErrorCode;

public enum NotificationErrorCode implements ErrorCode {

    EMAIL_REQUIRED(
        "NOTIFICATION_EMAIL_REQUIRED",
            "notification.email.required"
    ),
    EMAIL_INVALID(
            "NOTIFICATION_EMAIL_INVALID",
            "notification.email.invalid"
    ),
    SUBJECT_REQUIRED(
            "NOTIFICATION_SUBJECT_REQUIRED",
            "notification.subject.required"
    ),
    BODY_REQUIRED(
            "NOTIFICATION_BODY_REQUIRED",
            "notification.body.required"
    ),
    IDEMPOTENCY_KEY_REQUIRED(
            "NOTIFICATION_IDEMPOTENCY_KEY_REQUIRED",
            "notification.idempotency-key.required"
    ),
    IDEMPOTENCY_KEY_TOO_LONG(
            "NOTIFICATION_IDEMPOTENCY_KEY_TOO_LONG",
            "notification.idempotency-key.too-long"
    );

    private final String code;
    private final String messageKey;

    NotificationErrorCode(String code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String messageKey() {
        return messageKey;
    }
}
