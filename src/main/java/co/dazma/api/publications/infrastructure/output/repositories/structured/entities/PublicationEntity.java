package co.dazma.api.publications.infrastructure.output.repositories.structured.entities;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PublicationEntity(UUID id, UUID ownerId, String name, PublicationTypeEntity type, String description, String municipality,
                                PublicationStatusEntity status, Instant createdAt, List<PublicationPhotoEntity> photos, Integer price) {
}
