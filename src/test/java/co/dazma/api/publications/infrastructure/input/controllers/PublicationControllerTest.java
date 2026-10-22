package co.dazma.api.publications.infrastructure.input.controllers;

import co.dazma.api.publications.domain.Description;
import co.dazma.api.publications.domain.Price;
import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.Name;
import co.dazma.api.publications.domain.PublicationPhoto;
import co.dazma.api.publications.domain.PublicationType;
import co.dazma.api.publications.services.input.dto.CreatePublicationInputDto;
import co.dazma.api.publications.services.input.ports.CreatePublicationUseCase;
import co.dazma.api.shared.infrastructure.input.errors.ApiProblemFactory;
import co.dazma.api.shared.infrastructure.input.errors.ErrorMessageResolver;
import co.dazma.api.users.services.input.ports.ResolveUserIdUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        PublicationExceptionHandler exceptionHandler = new PublicationExceptionHandler(
                new ApiProblemFactory(new ErrorMessageResolver(messageSource))
        );

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new PublicationController(resolveUserIdUseCase, createPublicationUseCase)
                )
                .setControllerAdvice(exceptionHandler)
                .build();
    }

    @Test
    void shouldCreatePublicationForAuthenticatedUser() throws Exception {
        UUID ownerId = UUID.randomUUID();
        UUID photoId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-10-20T20:00:00Z");
        Publication publication = Publication.publish(
                ownerId,
                new Name("Bicicleta"),
                PublicationType.EXCHANGE,
                new Description("Bicicleta de montaña en buen estado"),
                new Municipality("Rionegro"),
                List.of(new PublicationPhoto(
                        photoId,
                        "publications/" + ownerId + "/" + photoId,
                        0
                )),
                createdAt,
                new Price(900_000)
        );
        CreatePublicationInputDto expectedInput = new CreatePublicationInputDto(
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
        when(createPublicationUseCase.create(expectedInput)).thenReturn(publication);

        mockMvc.perform(post("/api/publications")
                        .principal(() -> "firebase-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Bicicleta",
                                  "type": "EXCHANGE",
                                  "description": "Bicicleta de montaña en buen estado",
                                  "municipality": "Rionegro",
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
        verify(createPublicationUseCase).create(expectedInput);
    }

    @Test
    void shouldRejectRequestWithoutPriceBeforeCallingUseCase() throws Exception {
        UUID photoId = UUID.randomUUID();

        mockMvc.perform(post("/api/publications")
                        .principal(() -> "firebase-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Bicicleta",
                                  "type": "EXCHANGE",
                                  "description": "Bicicleta de montaña en buen estado",
                                  "municipality": "Rionegro",
                                  "photoUploadIds": ["%s"]
                                }
                                """.formatted(photoId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PUBLICATION_REQUEST_BODY_INVALID"))
                .andExpect(jsonPath("$.fields[0]").value("price"))
                .andExpect(jsonPath("$.instance").value("/api/publications"));

        verify(createPublicationUseCase, never()).create(any());
    }
}
