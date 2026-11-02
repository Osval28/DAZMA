package co.dazma.api.publications.services.usecases.impl;

import co.dazma.api.publications.domain.InvalidPublicationException;
import co.dazma.api.publications.domain.PhotoUploadRequest;
import co.dazma.api.publications.domain.PublicationErrorCode;
import co.dazma.api.publications.services.output.models.PhotoStorageUploadAuthorization;
import co.dazma.api.publications.services.output.ports.PhotoUploadParametersPort;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import co.dazma.api.publications.services.usecases.contract.AuthorizePublicationPhotoUploadUseCase;
import co.dazma.api.publications.services.usecases.models.PhotoUploadGrant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
public class AuthorizePublicationPhotoUploadService
        implements AuthorizePublicationPhotoUploadUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuthorizePublicationPhotoUploadService.class);

    private final OperatingOwnerResolver operatingOwners;
    private final PublicationPhotoStoragePort photoStorage;
    private final PhotoUploadParametersPort parameters;

    public AuthorizePublicationPhotoUploadService(
            OperatingOwnerResolver operatingOwners,
            PublicationPhotoStoragePort photoStorage,
            PhotoUploadParametersPort parameters
    ) {
        this.operatingOwners = Objects.requireNonNull(
                operatingOwners,
                "El resolutor de propietarios de publicaciones es obligatorio"
        );
        this.photoStorage = Objects.requireNonNull(
                photoStorage,
                "El almacenamiento de fotografías es obligatorio"
        );
        this.parameters = Objects.requireNonNull(
                parameters,
                "Los parámetros de carga de fotografías son obligatorios"
        );
    }

    @Override
    public PhotoUploadGrant execute(PhotoUploadRequest request) {
        UUID ownerId = operatingOwners.resolveId(request.ownerExternalId());
        String contentType = normalizeContentType(request.contentType());
        validateContentLength(request.contentLength());

        UUID photoId = UUID.randomUUID();

        PhotoStorageUploadAuthorization storageAuthorization = photoStorage.authorizeUpload(
                ownerId,
                photoId,
                contentType,
                request.contentLength(),
                parameters.uploadValidity()
        );

        PhotoUploadGrant grant = new PhotoUploadGrant(
                photoId,
                storageAuthorization.uploadUrl(),
                storageAuthorization.expiresAt()
        );
        log.atInfo()
                .addKeyValue("event.name", "publication.photo_upload.authorized")
                .addKeyValue("photo.id", grant.photoId())
                .addKeyValue("photo.content_type", contentType)
                .addKeyValue("photo.authorization.expires_at", grant.expiresAt())
                .log("Carga de fotografía autorizada");
        return grant;
    }

    private String normalizeContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new InvalidPublicationException(
                    PublicationErrorCode.PHOTO_CONTENT_TYPE_REQUIRED
            );
        }

        String normalizedContentType = contentType.trim().toLowerCase(Locale.ROOT);
        if (!parameters.allowedContentTypes().contains(normalizedContentType)) {
            throw new InvalidPublicationException(
                    PublicationErrorCode.PHOTO_CONTENT_TYPE_NOT_ALLOWED,
                    normalizedContentType
            );
        }
        return normalizedContentType;
    }

    private void validateContentLength(long contentLength) {
        if (contentLength <= 0) {
            throw new InvalidPublicationException(
                    PublicationErrorCode.PHOTO_CONTENT_LENGTH_INVALID
            );
        }
        long maximumContentLength = parameters.maximumContentLength();
        if (contentLength > maximumContentLength) {
            throw new InvalidPublicationException(
                    PublicationErrorCode.PHOTO_CONTENT_LENGTH_EXCEEDED,
                    maximumContentLength
            );
        }
    }
}
