package co.dazma.api.publications.services.output.ports;

import co.dazma.api.publications.services.output.models.PhotoStorageUploadAuthorization;
import co.dazma.api.publications.services.output.models.StoredPublicationPhoto;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface PublicationPhotoStoragePort {

    PhotoStorageUploadAuthorization authorizeUpload(
            UUID ownerId,
            UUID photoId,
            String contentType,
            long contentLength,
            Duration validity
    );

    Optional<StoredPublicationPhoto> findUploadedPhoto(UUID ownerId, UUID photoId);
}
