package co.dazma.api.publications.services.input.models;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

public record PublicationPhotoUploadAuthorization(
        UUID photoId,
        URI uploadUrl,
        Instant expiresAt
) {
}
