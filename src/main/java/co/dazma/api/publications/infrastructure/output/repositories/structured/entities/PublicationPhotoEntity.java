package co.dazma.api.publications.infrastructure.output.repositories.structured.entities;

import java.util.UUID;

public record PublicationPhotoEntity(UUID id, String objectKey, Integer position) {
}
