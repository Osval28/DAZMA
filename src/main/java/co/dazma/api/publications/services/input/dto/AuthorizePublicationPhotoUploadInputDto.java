package co.dazma.api.publications.services.input.dto;

import java.util.UUID;

public record AuthorizePublicationPhotoUploadInputDto(
        UUID ownerId,
        String contentType,
        Long contentLength
) {
}
