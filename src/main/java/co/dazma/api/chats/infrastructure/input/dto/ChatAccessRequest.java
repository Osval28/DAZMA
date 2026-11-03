package co.dazma.api.chats.infrastructure.input.dto;

import java.util.UUID;

public record ChatAccessRequest(String externalId, UUID roomId) {
}
