package co.dazma.api.publications.infrastructure.output.repositories.nonstructured.cache.entities;

import java.util.UUID;

public record PublicationPhotoCacheEntity(UUID id, String objectKey, Integer position) {
}