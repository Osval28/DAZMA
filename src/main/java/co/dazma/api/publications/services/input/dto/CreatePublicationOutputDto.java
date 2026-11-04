package co.dazma.api.publications.services.input.dto;

import co.dazma.api.publications.domain.enums.PublicationStatus;

import java.time.Instant;
import java.util.UUID;

public record CreatePublicationOutputDto(
        UUID id,
        PublicationStatus status,
        Instant createdAt
) {
}
