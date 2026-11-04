package co.dazma.api.publications.services.output.ports;

import co.dazma.api.publications.services.output.models.PhotoStorageUploadAuthorization;
import co.dazma.api.publications.domain.enums.PhotoInspectionStatus;
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

    /**
     * Copia a la zona definitiva únicamente una fotografía verificada por el escáner.
     * Devuelve vacío si la fotografía aún no fue aprobada.
     */
    Optional<StoredPublicationPhoto> promoteUploadedPhoto(UUID ownerId, UUID photoId);

    PhotoInspectionStatus inspectionStatus(UUID ownerId, UUID photoId);

    void discardPhotos(List<String> objectKeys);

    /**
     * Genera una URL temporal de solo lectura para una fotografía ya publicada.
     */
    URI authorizeDownload(String objectKey, Duration validity);
}
