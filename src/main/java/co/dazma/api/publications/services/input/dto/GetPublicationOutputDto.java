package co.dazma.api.publications.services.input.dto;

import co.dazma.api.publications.domain.PublicationStatus;
import co.dazma.api.publications.domain.PublicationType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GetPublicationOutputDto(
        UUID id,
        UUID ownerId,
        String ownerName,
        String name,
        PublicationType type,
        String description,
        String municipality,
        PublicationStatus status,
        Instant createdAt,
        Integer price,
        List<GetPublicationPhotoOutputDto> photos
) {
}
