package co.dazma.api.chats.infrastructure.input.dto;

import co.dazma.api.chats.services.input.dto.IssueChatTokenOutputDto;

public record ChatTokenResponseDto(
        String keyName,
        long ttl,
        long timestamp,
        String capability,
        String clientId,
        String nonce,
        String mac
) {

    public static ChatTokenResponseDto from(IssueChatTokenOutputDto token) {
        return new ChatTokenResponseDto(
                token.keyName(),
                token.ttl(),
                token.timestamp(),
                token.capability(),
                token.clientId(),
                token.nonce(),
                token.mac()
        );
    }
}
