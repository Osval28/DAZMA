package co.dazma.api.chats.infrastructure.input.dto;

import co.dazma.api.chats.services.input.dto.OpenChatRoomOutputDto;

import java.util.UUID;

public record OpenChatRoomResponseDto(UUID id) {

    public static OpenChatRoomResponseDto from(OpenChatRoomOutputDto room) {
        return new OpenChatRoomResponseDto(room.id());
    }
}
