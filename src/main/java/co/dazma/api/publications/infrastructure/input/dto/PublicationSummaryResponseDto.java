package co.dazma.api.publications.infrastructure.input.dto;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

public record PublicationSummaryResponseDto(
        UUID id,
        String name,
        PublicationTypeDto type,
        String municipality,
        Integer price,
        URI coverUrl,
        Instant createdAt
) {
}
