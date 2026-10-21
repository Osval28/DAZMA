package co.dazma.api.publications.infrastructure.output.repositories.nonstructured.cache.entities;

import co.dazma.api.publications.domain.PublicationStatus;
import co.dazma.api.publications.domain.PublicationType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PublicationCacheEntity(UUID id, UUID ownerId, String name, PublicationType type, String description,
                                     String city, PublicationStatus status, Instant createdAt,
                                     List<PublicationPhotoCacheEntity> photos, Integer price) {
}