package co.dazma.api.chats.infrastructure.output.externalservices.ably;

import co.dazma.api.chats.domain.ChatParticipant;
import co.dazma.api.chats.domain.ChatParticipantState;
import co.dazma.api.chats.domain.ChatRoom;
import co.dazma.api.chats.infrastructure.configuration.AblyProperties;
import co.dazma.api.chats.services.output.models.ChatTokenRequest;
import io.ably.lib.rest.AblyRest;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AblyChatTokenIssuerAdapterTest {

    @Test
    void shouldSignATokenRequestLimitedToTheRoomOfTheParticipant() throws Exception {
        AblyProperties properties = new AblyProperties("test-app.test-key:test-secret", Duration.ofMinutes(30), "dazma-chat");
        AblyChatTokenIssuerAdapter adapter = new AblyChatTokenIssuerAdapter(new AblyRest(properties.apiKey()), properties);
        ChatParticipant participant = new ChatParticipant(UUID.randomUUID(), "firebase-123", ChatParticipantState.ACTIVE);
        ChatRoom room = ChatRoom.open(UUID.randomUUID(), UUID.randomUUID(), participant.userId(), Instant.parse("2026-11-01T12:00:00Z"));

        ChatTokenRequest tokenRequest = adapter.issue(room, participant);

        assertEquals("test-app.test-key", tokenRequest.keyName());
        assertEquals(participant.userId().toString(), tokenRequest.clientId());
        assertEquals(Duration.ofMinutes(30).toMillis(), tokenRequest.ttl());
        assertTrue(tokenRequest.capability().contains("dazma-chat:" + room.getId()));
        assertFalse(tokenRequest.mac().isBlank());
        assertFalse(tokenRequest.nonce().isBlank());
    }
}
