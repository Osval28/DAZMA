package co.dazma.api.publications.services.output.ports;

import co.dazma.api.publications.services.output.models.PhotoStorageUploadAuthorization;
import co.dazma.api.publications.services.output.models.StoredPublicationPhoto;

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

    /**
     * Confirma que la fotografía fue subida y la mueve de la zona temporal a la definitiva.
     * Devuelve vacío si el propietario nunca subió esa fotografía.
     */
    Optional<StoredPublicationPhoto> promoteUploadedPhoto(UUID ownerId, UUID photoId);

    void discardPhotos(List<String> objectKeys);
}
