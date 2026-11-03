package co.dazma.api.publications.infrastructure.input.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
        @NotEmpty @Size(min = 1)
        @NotNull List<@NotNull UUID> photoUploadIds
) {
}
