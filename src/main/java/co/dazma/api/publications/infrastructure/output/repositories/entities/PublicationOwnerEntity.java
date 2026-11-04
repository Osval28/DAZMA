package co.dazma.api.publications.infrastructure.output.repositories.entities;

import java.util.UUID;

public record PublicationOwnerEntity(UUID userId, String userName, String externalId, PublicationOwnerStateEntity state) {
}
