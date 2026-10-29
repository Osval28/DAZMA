package co.dazma.api.chats.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatRoomTest {

    private static final Instant CREATED_AT = Instant.parse("2026-10-27T12:00:00Z");

    private final UUID publicationId = UUID.randomUUID();
    private final UUID ownerId = UUID.randomUUID();
    private final UUID interestedId = UUID.randomUUID();

    @Test
    void shouldOpenAValidChatRoom() {
        ChatRoom chatRoom = ChatRoom.open(publicationId, ownerId, interestedId, CREATED_AT);

        assertNotNull(chatRoom.getId());
        assertEquals(publicationId, chatRoom.getPublicationId());
        assertEquals(ownerId, chatRoom.getOwnerId());
        assertEquals(interestedId, chatRoom.getInterestedId());
        assertEquals(CREATED_AT, chatRoom.getCreatedAt());
    }

    @Test
    void shouldGiveADifferentIdToEachNewChatRoom() {
        ChatRoom first = ChatRoom.open(publicationId, ownerId, interestedId, CREATED_AT);
        ChatRoom second = ChatRoom.open(publicationId, ownerId, interestedId, CREATED_AT);

        assertNotEquals(first.getId(), second.getId());
    }

    @Test
    void shouldRestoreAChatRoomKeepingItsId() {
        UUID id = UUID.randomUUID();

        ChatRoom chatRoom = ChatRoom.restore(id, publicationId, ownerId, interestedId, CREATED_AT);

        assertEquals(id, chatRoom.getId());
    }

    @Test
    void shouldRejectAChatWithYourself() {
        SelfChatNotAllowedException exception = assertThrows(
                SelfChatNotAllowedException.class,
                () -> ChatRoom.open(publicationId, ownerId, ownerId, CREATED_AT)
        );

        assertEquals("CHAT_SELF_NOT_ALLOWED", exception.code());
    }

    @Test
    void shouldRejectMissingData() {
        assertMissing(ChatErrorCode.ROOM_ID_REQUIRED,
                () -> ChatRoom.restore(null, publicationId, ownerId, interestedId, CREATED_AT));
        assertMissing(ChatErrorCode.PUBLICATION_REQUIRED,
                () -> ChatRoom.open(null, ownerId, interestedId, CREATED_AT));
        assertMissing(ChatErrorCode.OWNER_REQUIRED,
                () -> ChatRoom.open(publicationId, null, interestedId, CREATED_AT));
        assertMissing(ChatErrorCode.INTERESTED_REQUIRED,
                () -> ChatRoom.open(publicationId, ownerId, null, CREATED_AT));
        assertMissing(ChatErrorCode.CREATED_AT_REQUIRED,
                () -> ChatRoom.open(publicationId, ownerId, interestedId, null));
    }

    @Test
    void shouldRecognizeOnlyItsTwoParticipants() {
        ChatRoom chatRoom = ChatRoom.open(publicationId, ownerId, interestedId, CREATED_AT);

        assertTrue(chatRoom.hasParticipant(ownerId));
        assertTrue(chatRoom.hasParticipant(interestedId));
        assertFalse(chatRoom.hasParticipant(UUID.randomUUID()));
        assertFalse(chatRoom.hasParticipant(null));
    }

    @Test
    void shouldBeEqualWhenItHasTheSameId() {
        UUID id = UUID.randomUUID();
        ChatRoom chatRoom = ChatRoom.restore(id, publicationId, ownerId, interestedId, CREATED_AT);
        ChatRoom sameRoom = ChatRoom.restore(id, publicationId, ownerId, interestedId, CREATED_AT);

        assertEquals(chatRoom, sameRoom);
        assertEquals(chatRoom.hashCode(), sameRoom.hashCode());
    }

    private void assertMissing(ChatErrorCode expectedError, Runnable action) {
        InvalidChatRoomException exception = assertThrows(InvalidChatRoomException.class, action::run);
        assertEquals(expectedError.code(), exception.code());
    }
}