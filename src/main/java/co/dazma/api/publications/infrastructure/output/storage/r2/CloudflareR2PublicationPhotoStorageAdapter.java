package co.dazma.api.publications.infrastructure.output.storage.r2;

import co.dazma.api.publications.services.output.models.PhotoStorageUploadAuthorization;
import co.dazma.api.publications.domain.enums.PhotoInspectionStatus;
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
import java.util.Optional;
import java.util.UUID;

public final class CloudflareR2PublicationPhotoStorageAdapter
        implements PublicationPhotoStoragePort {

    private static final String PENDING_PREFIX = "pending";
    private static final String READY_PREFIX = "ready";
    private static final String REJECTED_PREFIX = "rejected";
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
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;
        this.observations = observations;
    }

    @Override
    public PhotoStorageUploadAuthorization authorizeUpload(
            UUID ownerId,
            UUID photoId,
            String contentType,
            long contentLength,
            Duration validity
    ) {
        String objectKey = createPendingObjectKey(ownerId, photoId);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
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
        // Only the scanner can create ready objects. Pending uploads are never publishable.
        String readyKey = READY_PREFIX + "/" + ownerId + "/" + photoId;
        HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(readyKey)
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
                        .sourceKey(readyKey)
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
    public PhotoInspectionStatus inspectionStatus(UUID ownerId, UUID photoId) {
        if (exists(READY_PREFIX, ownerId, photoId)) return PhotoInspectionStatus.READY;
        if (exists(REJECTED_PREFIX, ownerId, photoId)) return PhotoInspectionStatus.REJECTED;
        if (exists(PENDING_PREFIX, ownerId, photoId)) return PhotoInspectionStatus.PENDING;
        return PhotoInspectionStatus.MISSING;
    }

    private boolean exists(String prefix, UUID ownerId, UUID photoId) {
        HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucketName)
                .key(prefix + "/" + ownerId + "/" + photoId).build();
        try {
            observations.observe("cloudflare_r2", "head_object", () -> s3Client.headObject(request));
            return true;
        } catch (S3Exception exception) {
            if (exception.statusCode() == 404) return false;
            throw exception;
        }
    }

    @Override
    public void discardPhotos(List<String> objectKeys) {
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
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(validity)
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
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
}
