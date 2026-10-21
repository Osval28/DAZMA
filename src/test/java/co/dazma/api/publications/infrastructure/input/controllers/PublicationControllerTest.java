package co.dazma.api.publications.infrastructure.input.controllers;

import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationPhoto;
import co.dazma.api.publications.domain.PublicationType;
import co.dazma.api.publications.services.input.models.CreatePublicationCommand;
import co.dazma.api.publications.services.input.ports.CreatePublicationUseCase;
import co.dazma.api.users.services.input.ports.ResolveUserIdUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PublicationControllerTest {

    private ResolveUserIdUseCase resolveUserIdUseCase;
    private CreatePublicationUseCase createPublicationUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        resolveUserIdUseCase = mock(ResolveUserIdUseCase.class);
        createPublicationUseCase = mock(CreatePublicationUseCase.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
                new PublicationController(resolveUserIdUseCase, createPublicationUseCase)
        ).build();
    }

    @Test
    void shouldCreatePublicationForAuthenticatedUser() throws Exception {
        UUID ownerId = UUID.randomUUID();
        UUID photoId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-10-20T20:00:00Z");
        Publication publication = Publication.publish(
                ownerId,
                "Bicicleta",
                PublicationType.EXCHANGE,
                "Bicicleta de montaña en buen estado",
                "Rionegro",
                List.of(new PublicationPhoto(
                        photoId,
                        "publications/" + ownerId + "/" + photoId,
                        0
                )),
                createdAt,
                900_000
        );
        CreatePublicationCommand expectedCommand = new CreatePublicationCommand(
                ownerId,
                "Bicicleta",
                PublicationType.EXCHANGE,
                "Bicicleta de montaña en buen estado",
                "Rionegro",
                900_000,
                List.of(photoId)
        );
        when(resolveUserIdUseCase.resolveByExternalId("firebase-123"))
                .thenReturn(ownerId);
        when(createPublicationUseCase.create(expectedCommand)).thenReturn(publication);

        mockMvc.perform(post("/api/publications")
                        .principal(() -> "firebase-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Bicicleta",
                                  "type": "EXCHANGE",
                                  "description": "Bicicleta de montaña en buen estado",
                                  "city": "Rionegro",
                                  "price": 900000,
                                  "photoUploadIds": ["%s"]
                                }
                                """.formatted(photoId)))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "http://localhost/api/publications/" + publication.getId()
                ))
                .andExpect(jsonPath("$.id").value(publication.getId().toString()))
                .andExpect(jsonPath("$.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.createdAt").value(createdAt.toString()));

        verify(resolveUserIdUseCase).resolveByExternalId("firebase-123");
        verify(createPublicationUseCase).create(expectedCommand);
    }
}
