package co.dazma.api.chats.infrastructure.output.repositories.entities;

import java.util.UUID;

public record ChatOfferEntity(UUID publicationId, UUID ownerId) {
}
