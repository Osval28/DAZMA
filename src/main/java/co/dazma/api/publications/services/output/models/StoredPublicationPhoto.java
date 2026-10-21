package co.dazma.api.publications.services.output.models;

import java.util.UUID;

public record StoredPublicationPhoto(
        UUID photoId,
        String objectKey,
        String contentType,
        long contentLength
) {
}
