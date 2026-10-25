package co.dazma.api.notifications.domain;

import java.util.regex.Pattern;

public record EmailMessage(String to, String subject, String body, String idempotencyKey) {

    private static final int IDEMPOTENCY_KEY_MAX_LENGTH = 256;
    private static final Pattern EMAIL_FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public EmailMessage {
        if (to.isBlank()) {
            throw new InvalidEmailMessageException(NotificationErrorCode.EMAIL_REQUIRED);
        }
        if (!EMAIL_FORMAT.matcher(to).matches()) {
            throw new InvalidEmailMessageException(NotificationErrorCode.EMAIL_INVALID);
        }
        if (subject.isBlank()) {
            throw new InvalidEmailMessageException(NotificationErrorCode.SUBJECT_REQUIRED);
        }
        if (body.isBlank()) {
            throw new InvalidEmailMessageException(NotificationErrorCode.BODY_REQUIRED);
        }
        if (idempotencyKey.isBlank()) {
            throw new InvalidEmailMessageException(NotificationErrorCode.IDEMPOTENCY_KEY_REQUIRED);
        }
        if (idempotencyKey.length() > IDEMPOTENCY_KEY_MAX_LENGTH) {
            throw new InvalidEmailMessageException(NotificationErrorCode.IDEMPOTENCY_KEY_TOO_LONG, IDEMPOTENCY_KEY_MAX_LENGTH);
        }
    }
}
