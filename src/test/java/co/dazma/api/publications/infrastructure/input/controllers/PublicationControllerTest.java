package co.dazma.api.publications.infrastructure.input.controllers;

import co.dazma.api.publications.domain.PublicationStatus;
import co.dazma.api.publications.domain.PublicationType;
import co.dazma.api.publications.infrastructure.input.mapper.PublicationWebMapper;
import co.dazma.api.publications.services.input.dto.CreatePublicationInputDto;
import co.dazma.api.publications.services.input.dto.CreatePublicationOutputDto;
import co.dazma.api.publications.services.input.dto.GetPublicationOutputDto;
import co.dazma.api.publications.services.input.dto.GetPublicationPhotoOutputDto;
import co.dazma.api.publications.domain.PublicationNotFoundException;
import co.dazma.api.publications.services.input.ports.CreatePublicationUseCase;
import co.dazma.api.publications.services.input.ports.GetPublicationUseCase;
import co.dazma.api.shared.infrastructure.input.errors.ApiProblemFactory;
import co.dazma.api.shared.infrastructure.input.errors.ErrorMessageResolver;
import co.dazma.api.shared.infrastructure.input.errors.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PublicationControllerTest {

    private CreatePublicationUseCase createPublicationUseCase;
    private GetPublicationUseCase getPublicationUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        createPublicationUseCase = mock(CreatePublicationUseCase.class);
        getPublicationUseCase = mock(GetPublicationUseCase.class);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(
                new ApiProblemFactory(new ErrorMessageResolver(messageSource))
        );

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new PublicationController(
                                createPublicationUseCase,
                                getPublicationUseCase,
                                Mappers.getMapper(PublicationWebMapper.class)
                        )
                )
                .setControllerAdvice(exceptionHandler)
                .build();
    }

    @Test
    void shouldCreatePublicationForAuthenticatedUser() throws Exception {
        UUID photoId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-10-20T20:00:00Z");
        CreatePublicationOutputDto publication = new CreatePublicationOutputDto(
                UUID.randomUUID(),
                PublicationStatus.PUBLISHED,
                createdAt
        );
        CreatePublicationInputDto expectedInput = new CreatePublicationInputDto(
                "firebase-123",
                "Bicicleta",
                PublicationType.EXCHANGE,
                "Bicicleta de montaña en buen estado",
                "Rionegro",
                900_000,
                List.of(photoId)
        );
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
                        "http://localhost/api/publications/" + publication.id()
                ))
                .andExpect(jsonPath("$.id").value(publication.id().toString()))
                .andExpect(jsonPath("$.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.createdAt").value(createdAt.toString()));

        verify(createPublicationUseCase).create(expectedInput);
    }

    @Test
    void shouldRejectRequestWithoutPriceBeforeCallingUseCase() throws Exception {
        mockMvc.perform(post("/api/publications")
                        .principal(() -> "firebase-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestWithoutPrice()))
                .andExpect(status().isBadRequest());

        verify(createPublicationUseCase, never()).create(any());
    }

    @Disabled("GlobalExceptionHandler todavía no maneja MethodArgumentNotValidException")
    @Test
    void shouldDescribeTheInvalidFieldsInTheProblemDetail() throws Exception {
        mockMvc.perform(post("/api/publications")
                        .principal(() -> "firebase-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestWithoutPrice()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PUBLICATION_REQUEST_BODY_INVALID"))
                .andExpect(jsonPath("$.fields[0]").value("price"))
                .andExpect(jsonPath("$.instance").value("/api/publications"));
    }

    private String requestWithoutPrice() {
        return """
                {
                  "name": "Bicicleta",
                  "type": "EXCHANGE",
                  "description": "Bicicleta de montaña en buen estado",
                  "municipality": "Rionegro",
                  "photoUploadIds": ["%s"]
                }
                """.formatted(UUID.randomUUID());
    }

    @Test
    void shouldReturnPublicationById() throws Exception {
        UUID publicationId = UUID.randomUUID();
        UUID photoId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        when(getPublicationUseCase.getById(publicationId)).thenReturn(new GetPublicationOutputDto(
                publicationId,
                ownerId,
                "Bicicleta",
                PublicationType.EXCHANGE,
                "Bicicleta de montaña en buen estado",
                "Rionegro",
                PublicationStatus.PUBLISHED,
                Instant.parse("2026-10-20T20:00:00Z"),
                900_000,
                List.of(new GetPublicationPhotoOutputDto(
                        photoId,
                        java.net.URI.create("https://storage.example/signed"),
                        0
                ))
        ));

        mockMvc.perform(get("/api/publications/{publicationId}", publicationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(publicationId.toString()))
                .andExpect(jsonPath("$.type").value("EXCHANGE"))
                .andExpect(jsonPath("$.municipality").value("Rionegro"))
                .andExpect(jsonPath("$.price").value(900_000))
                .andExpect(jsonPath("$.owner.id").value(ownerId.toString()))
                .andExpect(jsonPath("$.ownerId").doesNotExist())
                .andExpect(jsonPath("$.photos[0].id").value(photoId.toString()))
                .andExpect(jsonPath("$.photos[0].url").value("https://storage.example/signed"))
                .andExpect(jsonPath("$.photos[0].position").value(0));
    }

    @Test
    void shouldReturnNotFoundWhenPublicationDoesNotExist() throws Exception {
        UUID publicationId = UUID.randomUUID();
        when(getPublicationUseCase.getById(publicationId))
                .thenThrow(new PublicationNotFoundException(publicationId));

        mockMvc.perform(get("/api/publications/{publicationId}", publicationId))
                .andExpect(status().isNotFound());
    }
}
