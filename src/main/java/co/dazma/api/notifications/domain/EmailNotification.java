package co.dazma.api.notifications.domain;

import java.util.Map;
import java.util.UUID;

public record EmailNotification(
        NotificationType type,
        UUID recipientUserId,
        UUID referenceId,
        Map<String, String> data
) {

    public EmailNotification {
        data = Map.copyOf(data);
    }

    public String idempotencyKey() {
        return type.key() + "/" + referenceId;
    }
}
