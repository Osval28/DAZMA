package co.dazma.api.chats.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatParticipantTest {

    @Test
    void shouldOperateOnlyWhenActive() {
        assertTrue(new ChatParticipant(UUID.randomUUID(), "firebase-123", ChatParticipantState.ACTIVE).canOperate());
        assertFalse(new ChatParticipant(UUID.randomUUID(), "firebase-123", ChatParticipantState.SUSPENDED).canOperate());
    }

    @Test
    void shouldRequireAnExternalId() {
        InvalidChatParticipantException exception = assertThrows(
                InvalidChatParticipantException.class,
                () -> new ChatParticipant(UUID.randomUUID(), " ", ChatParticipantState.ACTIVE)
        );

        assertEquals("CHAT_PARTICIPANT_EXTERNAL_ID_REQUIRED", exception.code());
    }
}