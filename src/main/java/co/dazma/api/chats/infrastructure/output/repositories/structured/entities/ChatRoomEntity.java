package co.dazma.api.chats.infrastructure.output.repositories.structured.entities;

import java.time.Instant;
import java.util.UUID;

public record ChatRoomEntity(
        UUID id,
        UUID publicationId,
        UUID ownerId,
        UUID interestedId,
        Instant createdAt
) {
}