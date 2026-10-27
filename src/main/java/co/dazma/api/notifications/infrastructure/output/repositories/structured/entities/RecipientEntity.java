package co.dazma.api.notifications.infrastructure.output.repositories.structured.entities;

import java.util.UUID;

public record RecipientEntity(UUID userId, String email) {
}
