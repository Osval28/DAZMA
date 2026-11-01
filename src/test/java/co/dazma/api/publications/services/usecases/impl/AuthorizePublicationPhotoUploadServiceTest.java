package co.dazma.api.publications.services.usecases.impl;

import co.dazma.api.publications.domain.InvalidPublicationException;
import co.dazma.api.publications.domain.PhotoUploadRequest;
import co.dazma.api.publications.domain.PublicationOwner;
import co.dazma.api.publications.domain.PublicationOwnerState;
import co.dazma.api.publications.services.output.models.PhotoStorageUploadAuthorization;
import co.dazma.api.publications.services.output.ports.PhotoUploadParametersPort;
import co.dazma.api.publications.services.output.ports.PublicationOwnerRepositoryPort;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import co.dazma.api.publications.services.usecases.models.PhotoUploadGrant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AuthorizePublicationPhotoUploadServiceTest {

    private static final long MAXIMUM_CONTENT_LENGTH = 5_000_000;
    private static final Duration UPLOAD_VALIDITY = Duration.ofMinutes(5);
    private static final String EXTERNAL_ID = "firebase-123";

    private PublicationOwnerRepositoryPort ownerRepository;
    private UUID ownerId;
    private PublicationPhotoStoragePort photoStorage;
    private AuthorizePublicationPhotoUploadService service;

    @BeforeEach
    void setUp() {
        ownerRepository = mock(PublicationOwnerRepositoryPort.class);
        ownerId = UUID.randomUUID();
        when(ownerRepository.findByExternalId(EXTERNAL_ID))
                .thenReturn(Optional.of(new PublicationOwner(ownerId, "Ana", EXTERNAL_ID, PublicationOwnerState.ACTIVE)));
        photoStorage = mock(PublicationPhotoStoragePort.class);
        PhotoUploadParametersPort parameters = mock(PhotoUploadParametersPort.class);
        when(parameters.allowedContentTypes()).thenReturn(Set.of("image/jpeg", "image/png", "image/webp"));
        when(parameters.maximumContentLength()).thenReturn(MAXIMUM_CONTENT_LENGTH);
        when(parameters.uploadValidity()).thenReturn(UPLOAD_VALIDITY);
        service = new AuthorizePublicationPhotoUploadService(
                new OperatingOwnerResolver(ownerRepository),
                photoStorage,
                parameters
        );
    }

    @Test
    void shouldAuthorizeAValidPhotoUpload() {
        Instant expiresAt = Instant.parse("2026-10-20T18:00:00Z");
        PhotoStorageUploadAuthorization storageAuthorization =
                new PhotoStorageUploadAuthorization(
                        "publications/owner/photo",
                        URI.create("https://storage.example/upload"),
                        expiresAt
                );
        when(photoStorage.authorizeUpload(
                eq(ownerId),
                any(UUID.class),
                eq("image/jpeg"),
                eq(2_000_000L),
                eq(UPLOAD_VALIDITY)
        )).thenReturn(storageAuthorization);

        PhotoUploadGrant result = service.execute(
                new PhotoUploadRequest(
                        EXTERNAL_ID,
                        " IMAGE/JPEG ",
                        2_000_000L
                )
        );

        assertEquals(storageAuthorization.uploadUrl(), result.uploadUrl());
        assertEquals(storageAuthorization.expiresAt(), result.expiresAt());
        assertNotNull(result.photoId());
        verify(photoStorage).authorizeUpload(
                eq(ownerId),
                any(UUID.class),
                eq("image/jpeg"),
                eq(2_000_000L),
                eq(UPLOAD_VALIDITY)
        );
    }


    @Test
    void shouldRejectMissingOrUnsupportedContentType() {

        InvalidPublicationException missingType = assertThrows(
                InvalidPublicationException.class,
                () -> service.execute(new PhotoUploadRequest(
                        EXTERNAL_ID,
                        " ",
                        1_000L
                ))
        );
        InvalidPublicationException unsupportedType = assertThrows(
                InvalidPublicationException.class,
                () -> service.execute(new PhotoUploadRequest(
                        EXTERNAL_ID,
                        "application/pdf",
                        1_000L
                ))
        );

        assertEquals("PUBLICATION_PHOTO_CONTENT_TYPE_REQUIRED", missingType.code());
        assertEquals("PUBLICATION_PHOTO_CONTENT_TYPE_NOT_ALLOWED", unsupportedType.code());
        verifyNoInteractions(photoStorage);
    }

    @Test
    void shouldRejectInvalidOrExcessiveContentLength() {

        InvalidPublicationException invalidLength = assertThrows(
                InvalidPublicationException.class,
                () -> service.execute(new PhotoUploadRequest(
                        EXTERNAL_ID,
                        "image/png",
                        0L
                ))
        );
        InvalidPublicationException excessiveLength = assertThrows(
                InvalidPublicationException.class,
                () -> service.execute(new PhotoUploadRequest(
                        EXTERNAL_ID,
                        "image/png",
                        MAXIMUM_CONTENT_LENGTH + 1
                ))
        );

        assertEquals("PUBLICATION_PHOTO_CONTENT_LENGTH_INVALID", invalidLength.code());
        assertEquals("PUBLICATION_PHOTO_CONTENT_LENGTH_EXCEEDED", excessiveLength.code());
        verify(photoStorage, never()).authorizeUpload(
                any(UUID.class),
                any(UUID.class),
                anyString(),
                org.mockito.ArgumentMatchers.anyLong(),
                eq(UPLOAD_VALIDITY)
        );
    }
}
