package co.dazma.api.chats.domain;

import java.util.UUID;

public record ChatAccessRequest(String externalId, UUID roomId) {
}
