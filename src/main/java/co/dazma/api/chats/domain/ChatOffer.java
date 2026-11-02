package co.dazma.api.chats.domain;

import java.util.UUID;

public record ChatOffer(UUID publicationId, UUID ownerId) {
}
