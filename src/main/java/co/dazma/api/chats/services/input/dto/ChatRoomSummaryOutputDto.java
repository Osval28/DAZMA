package co.dazma.api.chats.services.input.dto;

import java.util.UUID;

public record ChatRoomSummaryOutputDto(UUID id, String participantName) {
}
