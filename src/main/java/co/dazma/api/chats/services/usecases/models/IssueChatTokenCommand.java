package co.dazma.api.chats.services.usecases.models;

import java.util.UUID;

public record IssueChatTokenCommand(String externalId, UUID roomId) {
}
