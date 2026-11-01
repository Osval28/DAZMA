package co.dazma.api.publications.infrastructure.input.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GetPublicationResponseDto(
        UUID id,
        String name,
        PublicationTypeDto type,
        String description,
        String municipality,
        PublicationStatusDto status,
        UUID owner,
        String ownerName,
        List<PublicationPhotoResponseDto> photos,
        Instant createdAt,
        Integer price
) {
}
