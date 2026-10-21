package co.dazma.api.publications.infrastructure.input.dto;

import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationStatus;

import java.time.Instant;
import java.util.UUID;

public record CreatePublicationResponseDto(
        UUID id,
        PublicationStatus status,
        Instant createdAt
) {

    public static CreatePublicationResponseDto fromDomain(Publication publication) {
        return new CreatePublicationResponseDto(
                publication.getId(),
                publication.getStatus(),
                publication.getCreatedAt()
        );
    }
}
