package co.dazma.api.publications.services.usecases;

import co.dazma.api.publications.domain.InvalidPublicationException;
import co.dazma.api.publications.services.input.models.AuthorizePublicationPhotoUploadCommand;
import co.dazma.api.publications.services.input.models.PublicationPhotoUploadAuthorization;
import co.dazma.api.publications.services.output.models.PhotoStorageUploadAuthorization;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
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

    private PublicationPhotoStoragePort photoStorage;
    private AuthorizePublicationPhotoUploadService service;

    @BeforeEach
    void setUp() {
        photoStorage = mock(PublicationPhotoStoragePort.class);
        service = new AuthorizePublicationPhotoUploadService(
                photoStorage,
                Set.of("image/jpeg", "image/png", "image/webp"),
                MAXIMUM_CONTENT_LENGTH,
                UPLOAD_VALIDITY
        );
    }

    @Test
    void shouldAuthorizeAValidPhotoUpload() {
        UUID ownerId = UUID.randomUUID();
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

        PublicationPhotoUploadAuthorization result = service.authorize(
                new AuthorizePublicationPhotoUploadCommand(
                        ownerId,
                        " IMAGE/JPEG ",
                        2_000_000
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
    void shouldRejectMissingRequestOrOwner() {
        InvalidPublicationException missingRequest = assertThrows(
                InvalidPublicationException.class,
                () -> service.authorize(null)
        );
        InvalidPublicationException missingOwner = assertThrows(
                InvalidPublicationException.class,
                () -> service.authorize(new AuthorizePublicationPhotoUploadCommand(
                        null,
                        "image/jpeg",
                        1_000
                ))
        );

        assertEquals("PUBLICATION_PHOTO_UPLOAD_REQUEST_REQUIRED", missingRequest.code());
        assertEquals("PUBLICATION_PHOTO_UPLOAD_OWNER_REQUIRED", missingOwner.code());
        verifyNoInteractions(photoStorage);
    }

    @Test
    void shouldRejectMissingOrUnsupportedContentType() {
        UUID ownerId = UUID.randomUUID();

        InvalidPublicationException missingType = assertThrows(
                InvalidPublicationException.class,
                () -> service.authorize(new AuthorizePublicationPhotoUploadCommand(
                        ownerId,
                        " ",
                        1_000
                ))
        );
        InvalidPublicationException unsupportedType = assertThrows(
                InvalidPublicationException.class,
                () -> service.authorize(new AuthorizePublicationPhotoUploadCommand(
                        ownerId,
                        "application/pdf",
                        1_000
                ))
        );

        assertEquals("PUBLICATION_PHOTO_CONTENT_TYPE_REQUIRED", missingType.code());
        assertEquals("PUBLICATION_PHOTO_CONTENT_TYPE_NOT_ALLOWED", unsupportedType.code());
        verifyNoInteractions(photoStorage);
    }

    @Test
    void shouldRejectInvalidOrExcessiveContentLength() {
        UUID ownerId = UUID.randomUUID();

        InvalidPublicationException invalidLength = assertThrows(
                InvalidPublicationException.class,
                () -> service.authorize(new AuthorizePublicationPhotoUploadCommand(
                        ownerId,
                        "image/png",
                        0
                ))
        );
        InvalidPublicationException excessiveLength = assertThrows(
                InvalidPublicationException.class,
                () -> service.authorize(new AuthorizePublicationPhotoUploadCommand(
                        ownerId,
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
