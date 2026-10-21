package co.dazma.api.publications.services.output.models;

import java.net.URI;
import java.time.Instant;

public record PhotoStorageUploadAuthorization(
        String objectKey,
        URI uploadUrl,
        Instant expiresAt
) {
}
