package co.dazma.api.publications.services.input.dto;

import co.dazma.api.publications.domain.PublicationType;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

public record PublicationSummaryOutputDto(
        UUID id,
        String name,
        PublicationType type,
        String municipality,
        Integer price,
        URI coverUrl,
        Instant createdAt
) {
}
