package co.dazma.api.chats.services.input.dto;

import java.util.UUID;

public record IssueChatTokenInputDto(String externalId, UUID roomId) {
}
