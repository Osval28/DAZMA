package co.dazma.api.publications.infrastructure.input.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreatePublicationRequestDto(
        @NotNull(message = "{publication.name.required}") String name,
        @NotNull(message = "{publication.type.required}") PublicationTypeDto type,
        @NotNull(message = "{publication.description.required}") String description,
        @NotNull(message = "{publication.municipality.required}") String municipality,
        @NotNull(message = "{publication.price.required}") Integer price,
        @NotNull(message = "{publication.photos.required}") List<@NotNull UUID> photoUploadIds) {
}
