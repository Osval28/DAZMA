package co.dazma.api.publications.services.usecases.models;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

public record PhotoUploadGrant(UUID photoId, URI uploadUrl, Instant expiresAt) {
}
