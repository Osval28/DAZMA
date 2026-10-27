package co.dazma.api.chats.infrastructure.output.repositories.structured.entities;

import java.util.UUID;

public record ChatParticipantEntity(UUID userId, String externalId, ChatParticipantStateEntity state) {
}