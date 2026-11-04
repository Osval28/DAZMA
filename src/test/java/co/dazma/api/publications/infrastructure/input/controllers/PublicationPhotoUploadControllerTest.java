package co.dazma.api.publications.infrastructure.input.controllers;

import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadInputDto;
import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadOutputDto;
import co.dazma.api.publications.services.input.ports.AuthorizePublicationPhotoUploadInputPort;
import co.dazma.api.publications.services.input.ports.GetPhotoInspectionStatusInputPort;
import co.dazma.api.publications.domain.enums.PhotoInspectionStatus;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PublicationPhotoUploadControllerTest {

    private AuthorizePublicationPhotoUploadInputPort authorizeUploadInputPort;
    private GetPhotoInspectionStatusInputPort inspectionStatusInputPort;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        authorizeUploadInputPort = mock(AuthorizePublicationPhotoUploadInputPort.class);
        inspectionStatusInputPort = mock(GetPhotoInspectionStatusInputPort.class);
        PublicationPhotoUploadController controller = new PublicationPhotoUploadController(
                authorizeUploadInputPort, inspectionStatusInputPort
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void shouldReturnPhotoStatusForTheAuthenticatedOwner() throws Exception {
        UUID photoId = UUID.randomUUID();
        when(inspectionStatusInputPort.execute("firebase-123", photoId)).thenReturn(PhotoInspectionStatus.READY);
        mockMvc.perform(get("/publications/api/v1/photos/{photoId}/status", photoId)
                        .principal(() -> "firebase-123"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.status").value("READY"));
        verify(inspectionStatusInputPort).execute("firebase-123", photoId);
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

        mockMvc.perform(post("/publications/api/v1/photos/upload-authorizations")
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
