package co.dazma.api.publications.infrastructure.input.dto;

import co.dazma.api.publications.domain.PublicationStatus;
import co.dazma.api.publications.services.input.dto.CreatePublicationOutputDto;

import java.time.Instant;
import java.util.UUID;

public record CreatePublicationResponseDto(
        UUID id,
        PublicationStatus status,
        Instant createdAt
) {

    public static CreatePublicationResponseDto fromOutput(CreatePublicationOutputDto output) {
        return new CreatePublicationResponseDto(
                output.id(),
                output.status(),
                output.createdAt()
        );
    }
}
