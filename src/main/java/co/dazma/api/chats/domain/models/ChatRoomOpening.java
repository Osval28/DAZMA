package co.dazma.api.chats.domain.models;

import java.util.UUID;

public record ChatRoomOpening(String externalId, boolean emailVerified, UUID publicationId) {
}
