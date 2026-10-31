package co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.entities;

import co.dazma.api.chats.infrastructure.output.repositories.structured.entities.ChatParticipantStateEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "chat_participants", schema = "app_chats")
public class ChatParticipantEntityJPA {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "external_id", nullable = false, unique = true)
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatParticipantStateEntity state;

    protected ChatParticipantEntityJPA() {
    }

    public ChatParticipantEntityJPA(UUID userId, String externalId, ChatParticipantStateEntity state) {
        this.userId = userId;
        this.externalId = externalId;
        this.state = state;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getExternalId() {
        return externalId;
    }

    public ChatParticipantStateEntity getState() {
        return state;
    }
}