package co.dazma.api.publications.services.input.adapters;

import co.dazma.api.publications.domain.PhotoUploadRequest;
import co.dazma.api.publications.services.input.adapters.mappers.PhotoUploadMapper;
import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadInputDto;
import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadOutputDto;
import co.dazma.api.publications.services.usecases.contract.AuthorizePublicationPhotoUploadUseCase;
import co.dazma.api.publications.services.usecases.models.PhotoUploadGrant;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthorizePublicationPhotoUploadInputAdapterTest {

    @Test
    void shouldTranslateTheRequestAndTheGrant() {
        AuthorizePublicationPhotoUploadUseCase useCase = mock(AuthorizePublicationPhotoUploadUseCase.class);
        AuthorizePublicationPhotoUploadInputAdapter adapter =
                new AuthorizePublicationPhotoUploadInputAdapter(useCase, new PhotoUploadMapper());
        UUID photoId = UUID.randomUUID();
        URI uploadUrl = URI.create("https://storage.example/upload");
        Instant expiresAt = Instant.parse("2026-10-20T20:10:00Z");
        when(useCase.execute(new PhotoUploadRequest("firebase-123", "image/jpeg", 2_000_000L)))
                .thenReturn(new PhotoUploadGrant(photoId, uploadUrl, expiresAt));

        AuthorizePublicationPhotoUploadOutputDto output = adapter.execute(
                new AuthorizePublicationPhotoUploadInputDto("firebase-123", "image/jpeg", 2_000_000L)
        );

        assertEquals(new AuthorizePublicationPhotoUploadOutputDto(photoId, uploadUrl, expiresAt), output);
    }
}
