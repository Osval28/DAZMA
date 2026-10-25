package co.dazma.api.publications.infrastructure.input.dto;

import co.dazma.api.publications.domain.PublicationType;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreatePublicationRequestDto(
        @NotNull
        String name,
        @NotNull
        PublicationTypeDto type,
        @NotNull
        String description,
        @NotNull
        String municipality,
        @NotNull
        Integer price,
        @NotNull List<@NotNull UUID> photoUploadIds
) {
}
