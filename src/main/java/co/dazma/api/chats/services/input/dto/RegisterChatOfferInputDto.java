package co.dazma.api.chats.services.input.dto;

import java.util.UUID;

public record RegisterChatOfferInputDto(UUID publicationId, UUID ownerId) {
}
