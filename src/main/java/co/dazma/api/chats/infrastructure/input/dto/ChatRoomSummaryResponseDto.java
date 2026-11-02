package co.dazma.api.chats.infrastructure.input.dto;

import co.dazma.api.chats.services.input.dto.ChatRoomSummaryOutputDto;

import java.util.UUID;

public record ChatRoomSummaryResponseDto(UUID id, String participantName) {

    public static ChatRoomSummaryResponseDto from(ChatRoomSummaryOutputDto summary) {
        return new ChatRoomSummaryResponseDto(summary.id(), summary.participantName());
    }
}
