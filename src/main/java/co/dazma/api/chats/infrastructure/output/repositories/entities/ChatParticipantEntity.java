package co.dazma.api.chats.infrastructure.output.repositories.entities;

import java.util.UUID;

public record ChatParticipantEntity(UUID userId, String externalId, String name, ChatParticipantStateEntity state) {
}