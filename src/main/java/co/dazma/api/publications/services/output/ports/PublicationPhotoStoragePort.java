package co.dazma.api.publications.services.output.ports;

import co.dazma.api.publications.services.output.models.PhotoStorageUploadAuthorization;
import co.dazma.api.publications.services.output.models.StoredPublicationPhoto;

import java.net.URI;
import java.time.Duration;
import java.util.List;
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

    Optional<StoredPublicationPhoto> promoteUploadedPhoto(UUID ownerId, UUID photoId);

    void discardPhotos(List<String> objectKeys);

    URI authorizeDownload(String objectKey, Duration validity);
}
