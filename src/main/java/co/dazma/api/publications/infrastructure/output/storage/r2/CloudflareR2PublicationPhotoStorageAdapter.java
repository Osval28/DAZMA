package co.dazma.api.publications.infrastructure.output.storage.r2;

import co.dazma.api.publications.services.output.models.PhotoStorageUploadAuthorization;
import co.dazma.api.publications.services.output.models.StoredPublicationPhoto;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import co.dazma.api.shared.infrastructure.observability.ExternalDependencyObservation;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class CloudflareR2PublicationPhotoStorageAdapter
        implements PublicationPhotoStoragePort {

    private static final String PENDING_PREFIX = "pending";
    private static final String PUBLICATIONS_PREFIX = "publications";

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;
    private final ExternalDependencyObservation observations;

    public CloudflareR2PublicationPhotoStorageAdapter(
            S3Client s3Client,
            S3Presigner s3Presigner,
            String bucketName,
            ExternalDependencyObservation observations
    ) {
        this.s3Client = Objects.requireNonNull(s3Client, "El cliente de R2 es obligatorio");
        this.s3Presigner = Objects.requireNonNull(
                s3Presigner,
                "El generador de permisos de R2 es obligatorio"
        );
        this.bucketName = requireText(bucketName, "El nombre del bucket es obligatorio");
        this.observations = Objects.requireNonNull(
                observations,
                "El observador de dependencias es obligatorio"
        );
    }

    @Override
    public PhotoStorageUploadAuthorization authorizeUpload(
            UUID ownerId,
            UUID photoId,
            String contentType,
            long contentLength,
            Duration validity
    ) {
        Objects.requireNonNull(ownerId, "El propietario de la fotografía es obligatorio");
        Objects.requireNonNull(photoId, "El identificador de la fotografía es obligatorio");
        String normalizedContentType = requireText(
                contentType,
                "El tipo de contenido es obligatorio"
        );
        if (contentLength <= 0) {
            throw new IllegalArgumentException("El tamaño del contenido debe ser mayor que cero");
        }
        if (validity == null || validity.isZero() || validity.isNegative()) {
            throw new IllegalArgumentException("La vigencia de carga debe ser mayor que cero");
        }

        String objectKey = createPendingObjectKey(ownerId, photoId);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(normalizedContentType)
                .contentLength(contentLength)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(validity)
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = observations.observe(
                "cloudflare_r2",
                "presign_upload",
                () -> s3Presigner.presignPutObject(presignRequest)
        );

        return new PhotoStorageUploadAuthorization(
                objectKey,
                URI.create(presignedRequest.url().toString()),
                presignedRequest.expiration()
        );
    }

    @Override
    public Optional<StoredPublicationPhoto> promoteUploadedPhoto(UUID ownerId, UUID photoId) {
        Objects.requireNonNull(ownerId, "El propietario de la fotografía es obligatorio");
        Objects.requireNonNull(photoId, "El identificador de la fotografía es obligatorio");
        String pendingKey = createPendingObjectKey(ownerId, photoId);
        HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(pendingKey)
                .build();

        HeadObjectResponse response;
        try {
            response = observations.observe(
                    "cloudflare_r2",
                    "head_object",
                    () -> s3Client.headObject(headRequest)
            );
        } catch (S3Exception exception) {
            if (exception.statusCode() == 404) {
                return Optional.empty();
            }
            throw exception;
        }

        String publishedKey = createPublishedObjectKey(ownerId);
        observations.observe(
                "cloudflare_r2",
                "copy_object",
                () -> s3Client.copyObject(CopyObjectRequest.builder()
                        .sourceBucket(bucketName)
                        .sourceKey(pendingKey)
                        .destinationBucket(bucketName)
                        .destinationKey(publishedKey)
                        .build())
        );

        return Optional.of(new StoredPublicationPhoto(
                photoId,
                publishedKey,
                response.contentType(),
                response.contentLength()
        ));
    }

    @Override
    public void discardPhotos(List<String> objectKeys) {
        Objects.requireNonNull(objectKeys, "Las claves de las fotografías son obligatorias");
        for (String objectKey : objectKeys) {
            observations.observe(
                    "cloudflare_r2",
                    "delete_object",
                    () -> s3Client.deleteObject(DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build())
            );
        }
    }

    @Override
    public URI authorizeDownload(String objectKey, Duration validity) {
        String normalizedObjectKey = requireText(
                objectKey,
                "La clave de la fotografía es obligatoria"
        );
        if (validity == null || validity.isZero() || validity.isNegative()) {
            throw new IllegalArgumentException("La vigencia de descarga debe ser mayor que cero");
        }

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(validity)
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(normalizedObjectKey)
                        .build())
                .build();

        return observations.observe(
                "cloudflare_r2",
                "presign_download",
                () -> URI.create(s3Presigner.presignGetObject(presignRequest).url().toString())
        );
    }

    private static String createPendingObjectKey(UUID ownerId, UUID photoId) {
        return PENDING_PREFIX + "/" + ownerId + "/" + photoId;
    }

    private static String createPublishedObjectKey(UUID ownerId) {
        return PUBLICATIONS_PREFIX + "/" + ownerId + "/" + UUID.randomUUID();
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
