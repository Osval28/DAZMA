package co.dazma.api.chats.services.input.dto;

public record IssueChatTokenOutputDto(
        String keyName,
        long ttl,
        long timestamp,
        String capability,
        String clientId,
        String nonce,
        String mac
) {
}
