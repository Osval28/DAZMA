package co.dazma.api.chats.domain;

import java.time.Instant;
import java.util.UUID;

public class ChatRoom {

    private final UUID id;
    private final UUID publicationId;
    private final UUID ownerId;
    private final UUID interestedId;
    private final Instant createdAt;

    private ChatRoom(
            UUID id,
            UUID publicationId,
            UUID ownerId,
            UUID interestedId,
            Instant createdAt
    ) {
        this.id = required(id, ChatErrorCode.ROOM_ID_REQUIRED);
        this.publicationId = required(publicationId, ChatErrorCode.PUBLICATION_REQUIRED);
        this.ownerId = required(ownerId, ChatErrorCode.OWNER_REQUIRED);
        this.interestedId = required(interestedId, ChatErrorCode.INTERESTED_REQUIRED);
        this.createdAt = required(createdAt, ChatErrorCode.CREATED_AT_REQUIRED);

        if (this.ownerId.equals(this.interestedId)) {
            throw new SelfChatNotAllowedException();
        }
    }

    public static ChatRoom open(
            UUID publicationId,
            UUID ownerId,
            UUID interestedId,
            Instant createdAt
    ) {
        return new ChatRoom(UUID.randomUUID(), publicationId, ownerId, interestedId, createdAt);
    }

    public static ChatRoom restore(
            UUID id,
            UUID publicationId,
            UUID ownerId,
            UUID interestedId,
            Instant createdAt
    ) {
        return new ChatRoom(id, publicationId, ownerId, interestedId, createdAt);
    }

    public boolean hasParticipant(UUID userId) {
        return ownerId.equals(userId) || interestedId.equals(userId);
    }

    private static <T> T required(T value, ChatErrorCode errorCode) {
        if (value == null) {
            throw new InvalidChatRoomException(errorCode);
        }
        return value;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPublicationId() {
        return publicationId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public UUID getInterestedId() {
        return interestedId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ChatRoom chatRoom)) return false;
        return id.equals(chatRoom.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}