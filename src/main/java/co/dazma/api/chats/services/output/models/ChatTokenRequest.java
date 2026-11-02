package co.dazma.api.chats.services.output.models;

public record ChatTokenRequest(
        String keyName,
        long ttl,
        long timestamp,
        String capability,
        String clientId,
        String nonce,
        String mac
) {
}
