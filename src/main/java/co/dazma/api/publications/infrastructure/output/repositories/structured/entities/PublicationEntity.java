package co.dazma.api.publications.infrastructure.output.repositories.structured.entities;

import co.dazma.api.publications.domain.PublicationStatus;
import co.dazma.api.publications.domain.PublicationType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PublicationEntity(UUID id, UUID ownerId, String name, PublicationType type, String description, String municipality,
                                PublicationStatus status, Instant createdAt, List<PublicationPhotoEntity> photos, Integer price) {
}
