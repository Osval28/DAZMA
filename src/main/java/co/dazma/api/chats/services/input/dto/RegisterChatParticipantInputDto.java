package co.dazma.api.chats.services.input.dto;

import java.util.UUID;

public record RegisterChatParticipantInputDto(UUID userId, String externalId, String name, String state) {
}
