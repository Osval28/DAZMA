package co.dazma.api.publications.services.input.dto;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

public record AuthorizePublicationPhotoUploadOutputDto(
        UUID photoId,
        URI uploadUrl,
        Instant expiresAt
) {
}
