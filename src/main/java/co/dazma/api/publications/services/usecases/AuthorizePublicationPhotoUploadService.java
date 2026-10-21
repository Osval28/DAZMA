package co.dazma.api.publications.services.usecases;

import co.dazma.api.publications.domain.InvalidPublicationException;
import co.dazma.api.publications.domain.PublicationErrorCode;
import co.dazma.api.publications.services.input.models.AuthorizePublicationPhotoUploadCommand;
import co.dazma.api.publications.services.input.models.PublicationPhotoUploadAuthorization;
import co.dazma.api.publications.services.input.ports.AuthorizePublicationPhotoUploadUseCase;
import co.dazma.api.publications.services.output.models.PhotoStorageUploadAuthorization;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;

import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AuthorizePublicationPhotoUploadService
        implements AuthorizePublicationPhotoUploadUseCase {

    private final PublicationPhotoStoragePort photoStorage;
    private final Set<String> allowedContentTypes;
    private final long maximumContentLength;
    private final Duration uploadValidity;

    public AuthorizePublicationPhotoUploadService(
            PublicationPhotoStoragePort photoStorage,
            Set<String> allowedContentTypes,
            long maximumContentLength,
            Duration uploadValidity
    ) {
        this.photoStorage = Objects.requireNonNull(
                photoStorage,
                "El almacenamiento de fotografías es obligatorio"
        );
        this.allowedContentTypes = normalizeAllowedContentTypes(allowedContentTypes);
        if (maximumContentLength <= 0) {
            throw new IllegalArgumentException("El tamaño máximo debe ser mayor que cero");
        }
        this.maximumContentLength = maximumContentLength;
        if (uploadValidity == null || uploadValidity.isZero() || uploadValidity.isNegative()) {
            throw new IllegalArgumentException("La vigencia de carga debe ser mayor que cero");
        }
        this.uploadValidity = uploadValidity;
    }

    @Override
    public PublicationPhotoUploadAuthorization authorize(
            AuthorizePublicationPhotoUploadCommand command
    ) {
        if (command == null) {
            throw new InvalidPublicationException(
                    PublicationErrorCode.PHOTO_UPLOAD_REQUEST_REQUIRED
            );
        }
        if (command.ownerId() == null) {
            throw new InvalidPublicationException(
                    PublicationErrorCode.PHOTO_UPLOAD_OWNER_REQUIRED
            );
        }

        String contentType = normalizeContentType(command.contentType());
        validateContentLength(command.contentLength());

        UUID photoId = UUID.randomUUID();

        PhotoStorageUploadAuthorization storageAuthorization = photoStorage.authorizeUpload(
                command.ownerId(),
                photoId,
                contentType,
                command.contentLength(),
                uploadValidity
        );

        return new PublicationPhotoUploadAuthorization(
                photoId,
                storageAuthorization.uploadUrl(),
                storageAuthorization.expiresAt()
        );
    }

    private String normalizeContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new InvalidPublicationException(
                    PublicationErrorCode.PHOTO_CONTENT_TYPE_REQUIRED
            );
        }

        String normalizedContentType = contentType.trim().toLowerCase(Locale.ROOT);
        if (!allowedContentTypes.contains(normalizedContentType)) {
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
        if (contentLength > maximumContentLength) {
            throw new InvalidPublicationException(
                    PublicationErrorCode.PHOTO_CONTENT_LENGTH_EXCEEDED,
                    maximumContentLength
            );
        }
    }

    private static Set<String> normalizeAllowedContentTypes(Set<String> contentTypes) {
        if (contentTypes == null || contentTypes.isEmpty()
                || contentTypes.stream().anyMatch(type -> type == null || type.isBlank())) {
            throw new IllegalArgumentException("Debe configurarse al menos un tipo de imagen");
        }

        return contentTypes.stream()
                .map(type -> type.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }
}
