package co.dazma.api.publications.infrastructure.input.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthorizePublicationPhotoUploadRequestDto(
        @NotBlank
        String contentType,
        @NotNull Long contentLength
) {
}
