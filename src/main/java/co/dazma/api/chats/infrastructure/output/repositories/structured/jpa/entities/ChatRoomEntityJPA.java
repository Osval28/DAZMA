package co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_rooms", schema = "app_chats")
public class ChatRoomEntityJPA {

    @Id
    private UUID id;

    @Column(name = "publication_id", nullable = false)
    private UUID publicationId;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "interested_id", nullable = false)
    private UUID interestedId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ChatRoomEntityJPA() {
    }

    public ChatRoomEntityJPA(
            UUID id,
            UUID publicationId,
            UUID ownerId,
            UUID interestedId,
            Instant createdAt
    ) {
        this.id = id;
        this.publicationId = publicationId;
        this.ownerId = ownerId;
        this.interestedId = interestedId;
        this.createdAt = createdAt;
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
}