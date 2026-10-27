package co.dazma.api.publications.infrastructure.input.controllers;

import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadInputDto;
import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadOutputDto;
import co.dazma.api.publications.services.input.ports.AuthorizePublicationPhotoUploadInputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PublicationPhotoUploadControllerTest {

    private AuthorizePublicationPhotoUploadInputPort authorizeUploadInputPort;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        authorizeUploadInputPort = mock(AuthorizePublicationPhotoUploadInputPort.class);
        PublicationPhotoUploadController controller = new PublicationPhotoUploadController(
                authorizeUploadInputPort
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void shouldAuthorizeTheUploadForTheAuthenticatedUser() throws Exception {
        UUID photoId = UUID.randomUUID();
        Instant expiration = Instant.parse("2026-10-20T22:30:00Z");
        when(authorizeUploadInputPort.execute(
                new AuthorizePublicationPhotoUploadInputDto(
                        "firebase-123",
                        "image/jpeg",
                        2_000_000L
                )
        )).thenReturn(new AuthorizePublicationPhotoUploadOutputDto(
                photoId,
                URI.create("https://storage.example/upload"),
                expiration
        ));

        mockMvc.perform(post("/api/publications/photos/upload-authorizations")
                        .principal(() -> "firebase-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "contentType": "image/jpeg",
                                  "contentLength": 2000000
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.photoId").value(photoId.toString()))
                .andExpect(jsonPath("$.uploadUrl").value("https://storage.example/upload"))
                .andExpect(jsonPath("$.expiresAt").value("2026-10-20T22:30:00Z"));

        verify(authorizeUploadInputPort).execute(
                new AuthorizePublicationPhotoUploadInputDto(
                        "firebase-123",
                        "image/jpeg",
                        2_000_000L
                )
        );
    }
}
