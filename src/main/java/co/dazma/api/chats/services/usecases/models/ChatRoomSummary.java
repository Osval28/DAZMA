package co.dazma.api.chats.services.usecases.models;

import java.util.UUID;

public record ChatRoomSummary(UUID roomId, String participantName) {
}
