package co.dazma.api.publications.services.input.models;

import java.util.UUID;

public record AuthorizePublicationPhotoUploadCommand(
        UUID ownerId,
        String contentType,
        long contentLength
) {
}
