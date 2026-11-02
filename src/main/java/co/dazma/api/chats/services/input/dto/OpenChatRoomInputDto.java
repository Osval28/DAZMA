package co.dazma.api.chats.services.input.dto;

import java.util.UUID;

public record OpenChatRoomInputDto(String externalId, boolean emailVerified, UUID publicationId) {
}
