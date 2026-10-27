package co.dazma.api.notifications.domain;

import java.util.UUID;

public record Recipient(UUID userId, String email) {
}
