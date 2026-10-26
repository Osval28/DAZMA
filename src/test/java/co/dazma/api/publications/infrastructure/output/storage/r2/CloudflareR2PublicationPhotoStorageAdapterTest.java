package co.dazma.api.publications.infrastructure.output.storage.r2;

import co.dazma.api.publications.services.output.models.PhotoStorageUploadAuthorization;
import co.dazma.api.publications.services.output.models.StoredPublicationPhoto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

        String expectedObjectKey = "pending/" + ownerId + "/" + photoId;
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
    void shouldGenerateATemporaryDownloadUrlForAPublishedPhoto() throws Exception {
        Duration validity = Duration.ofMinutes(10);
        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(
                URI.create("https://storage.example/download").toURL()
        );
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(presignedRequest);

        URI url = adapter.authorizeDownload("publications/owner/photo", validity);

        ArgumentCaptor<GetObjectPresignRequest> captor =
                ArgumentCaptor.forClass(GetObjectPresignRequest.class);
        verify(s3Presigner).presignGetObject(captor.capture());
        assertEquals(URI.create("https://storage.example/download"), url);
        assertEquals(validity, captor.getValue().signatureDuration());
        assertEquals(BUCKET_NAME, captor.getValue().getObjectRequest().bucket());
        assertEquals("publications/owner/photo", captor.getValue().getObjectRequest().key());
    }

    @Test
    void shouldRejectADownloadWithoutKeyOrValidity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> adapter.authorizeDownload(" ", Duration.ofMinutes(10))
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> adapter.authorizeDownload("publications/owner/photo", Duration.ZERO)
        );
    }

    @Test
    void shouldPromoteAnUploadedPhotoFromPendingToPublished() {
        UUID ownerId = UUID.randomUUID();
        UUID photoId = UUID.randomUUID();
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder()
                        .contentType("image/jpeg")
                        .contentLength(2_000_000L)
                        .build());

        StoredPublicationPhoto storedPhoto = adapter
                .promoteUploadedPhoto(ownerId, photoId)
                .orElseThrow();

        ArgumentCaptor<HeadObjectRequest> head = ArgumentCaptor.forClass(HeadObjectRequest.class);
        verify(s3Client).headObject(head.capture());
        assertEquals(BUCKET_NAME, head.getValue().bucket());
        assertEquals("pending/" + ownerId + "/" + photoId, head.getValue().key());

        ArgumentCaptor<CopyObjectRequest> copy = ArgumentCaptor.forClass(CopyObjectRequest.class);
        verify(s3Client).copyObject(copy.capture());
        assertEquals(BUCKET_NAME, copy.getValue().sourceBucket());
        assertEquals("pending/" + ownerId + "/" + photoId, copy.getValue().sourceKey());
        assertEquals(BUCKET_NAME, copy.getValue().destinationBucket());
        assertTrue(copy.getValue().destinationKey().startsWith("publications/" + ownerId + "/"));

        assertEquals(photoId, storedPhoto.photoId());
        assertEquals(copy.getValue().destinationKey(), storedPhoto.objectKey());
        assertEquals("image/jpeg", storedPhoto.contentType());
        assertEquals(2_000_000L, storedPhoto.contentLength());
    }

    @Test
    void shouldUseADifferentPublishedKeyForEachPromotion() {
        UUID ownerId = UUID.randomUUID();
        UUID photoId = UUID.randomUUID();
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder()
                        .contentType("image/jpeg")
                        .contentLength(1L)
                        .build());

        String first = adapter.promoteUploadedPhoto(ownerId, photoId).orElseThrow().objectKey();
        String second = adapter.promoteUploadedPhoto(ownerId, photoId).orElseThrow().objectKey();

        assertNotEquals(first, second);
    }

    @Test
    void shouldReportThatAnObjectDoesNotExistWithoutCopying() {
        UUID ownerId = UUID.randomUUID();
        UUID photoId = UUID.randomUUID();
        S3Exception missingObject = mock(S3Exception.class);
        when(missingObject.statusCode()).thenReturn(404);
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(missingObject);

        assertTrue(adapter.promoteUploadedPhoto(ownerId, photoId).isEmpty());

        verify(s3Client, never()).copyObject(any(CopyObjectRequest.class));
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
                () -> adapter.promoteUploadedPhoto(ownerId, photoId)
        );

        assertSame(failure, thrown);
        verify(s3Client, never()).copyObject(any(CopyObjectRequest.class));
    }

    @Test
    void shouldDeleteEveryDiscardedPhoto() {
        adapter.discardPhotos(List.of("publications/a/1", "publications/a/2"));

        ArgumentCaptor<DeleteObjectRequest> captor =
                ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client, times(2)).deleteObject(captor.capture());
        assertEquals(BUCKET_NAME, captor.getAllValues().get(0).bucket());
        assertEquals("publications/a/1", captor.getAllValues().get(0).key());
        assertEquals("publications/a/2", captor.getAllValues().get(1).key());
    }
}
