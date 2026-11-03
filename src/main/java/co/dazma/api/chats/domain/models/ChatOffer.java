package co.dazma.api.chats.domain.models;

import java.util.UUID;

public record ChatOffer(UUID publicationId, UUID ownerId) {
}
