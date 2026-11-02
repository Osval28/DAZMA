package co.dazma.api.chats.infrastructure.output.externalservices.ably;

import co.dazma.api.chats.domain.ChatParticipant;
import co.dazma.api.chats.domain.ChatRoom;
import co.dazma.api.chats.infrastructure.configuration.AblyProperties;
import co.dazma.api.chats.services.output.models.ChatTokenRequest;
import co.dazma.api.chats.services.output.ports.ChatTokenIssuerPort;
import io.ably.lib.rest.AblyRest;
import io.ably.lib.rest.Auth;
import io.ably.lib.types.AblyException;

public class AblyChatTokenIssuerAdapter implements ChatTokenIssuerPort {

    private static final String ALLOWED_OPERATIONS = "[\"publish\",\"subscribe\",\"history\"]";
    private static final String CHANNEL_NAME_FORMAT = "%s:%s";
    private static final String CAPABILITY_FORMAT = "{\"%s\":%s,\"%s\":%s}";
    private static final String ROOM_CHANNELS_SUFFIX = "::*";

    private final AblyRest ably;
    private final AblyProperties properties;

    public AblyChatTokenIssuerAdapter(AblyRest ably, AblyProperties properties) {
        this.ably = ably;
        this.properties = properties;
    }

    @Override
    public ChatTokenRequest issue(ChatRoom room, ChatParticipant participant) {
        Auth.TokenParams parameters = new Auth.TokenParams();
        parameters.clientId = participant.userId().toString();
        parameters.ttl = properties.tokenValidity().toMillis();
        parameters.capability = capabilityFor(room);

        try {
            Auth.TokenRequest tokenRequest = ably.auth.createTokenRequest(parameters, null);
            return new ChatTokenRequest(
                    tokenRequest.keyName,
                    tokenRequest.ttl,
                    tokenRequest.timestamp,
                    tokenRequest.capability,
                    tokenRequest.clientId,
                    tokenRequest.nonce,
                    tokenRequest.mac
            );
        } catch (AblyException failure) {
            throw new ChatTokenIssuanceException(failure);
        }
    }

    private String capabilityFor(ChatRoom room) {
        String channel = CHANNEL_NAME_FORMAT.formatted(properties.roomNamespace(), room.getId());
        return CAPABILITY_FORMAT.formatted(
                channel,
                ALLOWED_OPERATIONS,
                channel + ROOM_CHANNELS_SUFFIX,
                ALLOWED_OPERATIONS
        );
    }
}
