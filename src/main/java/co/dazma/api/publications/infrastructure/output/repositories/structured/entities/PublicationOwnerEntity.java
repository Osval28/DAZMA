package co.dazma.api.publications.infrastructure.output.repositories.structured.entities;

import java.util.UUID;

public record PublicationOwnerEntity(UUID userId, String userName, String externalId, PublicationOwnerStateEntity state) {
}
