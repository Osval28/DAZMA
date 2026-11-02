package co.dazma.api.chats.infrastructure.output.repositories.structured.entities;

import java.util.UUID;

public record ChatOfferEntity(UUID publicationId, UUID ownerId) {
}
