package co.dazma.api.chats.infrastructure.input.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OpenChatRoomRequestDto(@NotNull UUID publicationId) {
}
