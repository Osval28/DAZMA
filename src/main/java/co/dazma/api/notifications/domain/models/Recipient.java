package co.dazma.api.notifications.domain.models;

import java.util.UUID;

public record Recipient(UUID userId, String email) {
}
