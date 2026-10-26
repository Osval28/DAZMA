package co.dazma.api.publications.infrastructure.output.repositories.nonstructured.cache.entities;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PublicationCacheEntity(UUID id, UUID ownerId, String name, PublicationTypeCacheEntity type, String description,
                                     String municipality, PublicationStatusCacheEntity status, Instant createdAt,
                                     List<PublicationPhotoCacheEntity> photos, Integer price) {
}
