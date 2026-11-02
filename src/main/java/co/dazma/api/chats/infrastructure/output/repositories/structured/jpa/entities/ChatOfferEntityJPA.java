package co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "chat_offers", schema = "app_chats")
public class ChatOfferEntityJPA {

    @Id
    @Column(name = "publication_id")
    private UUID publicationId;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    protected ChatOfferEntityJPA() {
    }

    public ChatOfferEntityJPA(UUID publicationId, UUID ownerId) {
        this.publicationId = publicationId;
        this.ownerId = ownerId;
    }

    public UUID getPublicationId() {
        return publicationId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }
}
