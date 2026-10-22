package co.dazma.api.publications.infrastructure.input.dto;

import jakarta.validation.constraints.NotNull;

public record AuthorizePublicationPhotoUploadRequestDto(
        String contentType,
        @NotNull Long contentLength
) {
}
