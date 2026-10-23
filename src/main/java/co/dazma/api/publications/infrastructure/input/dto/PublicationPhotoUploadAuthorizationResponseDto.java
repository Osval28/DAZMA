package co.dazma.api.publications.infrastructure.input.dto;

import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadOutputDto;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

public record PublicationPhotoUploadAuthorizationResponseDto(
        UUID photoId,
        URI uploadUrl,
        Instant expiresAt
) {

    public static PublicationPhotoUploadAuthorizationResponseDto fromModel(
            AuthorizePublicationPhotoUploadOutputDto authorization
    ) {
        return new PublicationPhotoUploadAuthorizationResponseDto(
                authorization.photoId(),
                authorization.uploadUrl(),
                authorization.expiresAt()
        );
    }
}
