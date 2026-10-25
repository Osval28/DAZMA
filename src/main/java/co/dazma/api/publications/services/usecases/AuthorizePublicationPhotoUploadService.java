package co.dazma.api.publications.services.usecases;

import co.dazma.api.publications.domain.InvalidPublicationException;
import co.dazma.api.publications.domain.PublicationErrorCode;
import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadInputDto;
import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadOutputDto;
import co.dazma.api.publications.services.input.ports.AuthorizePublicationPhotoUploadUseCase;
import co.dazma.api.publications.services.output.models.PhotoStorageUploadAuthorization;
import co.dazma.api.publications.services.output.ports.PhotoUploadParametersPort;
import co.dazma.api.publications.services.output.ports.PublicationOwnerPort;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
public class AuthorizePublicationPhotoUploadService
        implements AuthorizePublicationPhotoUploadUseCase {

    private final PublicationOwnerPort publicationOwners;
    private final PublicationPhotoStoragePort photoStorage;
    private final PhotoUploadParametersPort parameters;

    public AuthorizePublicationPhotoUploadService(
            PublicationOwnerPort publicationOwners,
            PublicationPhotoStoragePort photoStorage,
            PhotoUploadParametersPort parameters
    ) {
        this.publicationOwners = Objects.requireNonNull(
                publicationOwners,
                "El puerto de propietarios de publicaciones es obligatorio"
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
    public AuthorizePublicationPhotoUploadOutputDto authorize(
            AuthorizePublicationPhotoUploadInputDto input
    ) {

        UUID ownerId = publicationOwners.resolveOperatingOwner(input.ownerExternalId());
        String contentType = normalizeContentType(input.contentType());
        validateContentLength(input.contentLength());

        UUID photoId = UUID.randomUUID();

        PhotoStorageUploadAuthorization storageAuthorization = photoStorage.authorizeUpload(
                ownerId,
                photoId,
                contentType,
                input.contentLength(),
                parameters.uploadValidity()
        );

        return new AuthorizePublicationPhotoUploadOutputDto(
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
