package co.dazma.api.publications.infrastructure.output.storage.r2;

import co.dazma.api.publications.services.output.models.PhotoStorageUploadAuthorization;
import co.dazma.api.publications.services.output.models.StoredPublicationPhoto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CloudflareR2PublicationPhotoStorageAdapterTest {

    private static final String BUCKET_NAME = "publication-photos";

    private S3Client s3Client;
    private S3Presigner s3Presigner;
    private CloudflareR2PublicationPhotoStorageAdapter adapter;

    @BeforeEach
    void setUp() {
        s3Client = mock(S3Client.class);
        s3Presigner = mock(S3Presigner.class);
        adapter = new CloudflareR2PublicationPhotoStorageAdapter(
                s3Client,
                s3Presigner,
                BUCKET_NAME
        );
    }

    @Test
    void shouldGenerateAnUploadAuthorizationForR2() throws Exception {
        UUID ownerId = UUID.randomUUID();
        UUID photoId = UUID.randomUUID();
        Duration validity = Duration.ofMinutes(5);
        Instant expiration = Instant.parse("2026-10-20T22:00:00Z");
        PresignedPutObjectRequest presignedRequest = mock(PresignedPutObjectRequest.class);

        when(presignedRequest.url()).thenReturn(
                URI.create("https://storage.example/upload").toURL()
        );
        when(presignedRequest.expiration()).thenReturn(expiration);
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenReturn(presignedRequest);

        PhotoStorageUploadAuthorization authorization = adapter.authorizeUpload(
                ownerId,
                photoId,
                "image/jpeg",
                2_000_000,
                validity
        );

        ArgumentCaptor<PutObjectPresignRequest> captor =
                ArgumentCaptor.forClass(PutObjectPresignRequest.class);
        verify(s3Presigner).presignPutObject(captor.capture());
        PutObjectPresignRequest request = captor.getValue();

        String expectedObjectKey = "publications/" + ownerId + "/" + photoId;
        assertEquals(expectedObjectKey, authorization.objectKey());
        assertEquals(URI.create("https://storage.example/upload"), authorization.uploadUrl());
        assertEquals(expiration, authorization.expiresAt());
        assertEquals(validity, request.signatureDuration());
        assertEquals(BUCKET_NAME, request.putObjectRequest().bucket());
        assertEquals(expectedObjectKey, request.putObjectRequest().key());
        assertEquals("image/jpeg", request.putObjectRequest().contentType());
        assertEquals(2_000_000, request.putObjectRequest().contentLength());
    }

    @Test
    void shouldConfirmThatAnObjectExists() {
        UUID ownerId = UUID.randomUUID();
        UUID photoId = UUID.randomUUID();
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder()
                        .contentType("image/jpeg")
                        .contentLength(2_000_000L)
                        .build());

        StoredPublicationPhoto storedPhoto = adapter
                .findUploadedPhoto(ownerId, photoId)
                .orElseThrow();

        ArgumentCaptor<HeadObjectRequest> captor =
                ArgumentCaptor.forClass(HeadObjectRequest.class);
        verify(s3Client).headObject(captor.capture());
        assertEquals(BUCKET_NAME, captor.getValue().bucket());
        assertEquals(
                "publications/" + ownerId + "/" + photoId,
                captor.getValue().key()
        );
        assertEquals(photoId, storedPhoto.photoId());
        assertEquals(captor.getValue().key(), storedPhoto.objectKey());
        assertEquals("image/jpeg", storedPhoto.contentType());
        assertEquals(2_000_000L, storedPhoto.contentLength());
    }

    @Test
    void shouldReportThatAnObjectDoesNotExist() {
        UUID ownerId = UUID.randomUUID();
        UUID photoId = UUID.randomUUID();
        S3Exception missingObject = mock(S3Exception.class);
        when(missingObject.statusCode()).thenReturn(404);
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(missingObject);

        assertTrue(adapter.findUploadedPhoto(ownerId, photoId).isEmpty());
    }

    @Test
    void shouldPropagateUnexpectedR2Failures() {
        UUID ownerId = UUID.randomUUID();
        UUID photoId = UUID.randomUUID();
        S3Exception failure = mock(S3Exception.class);
        when(failure.statusCode()).thenReturn(503);
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenThrow(failure);

        S3Exception thrown = assertThrows(
                S3Exception.class,
                () -> adapter.findUploadedPhoto(ownerId, photoId)
        );

        assertSame(failure, thrown);
    }
}
