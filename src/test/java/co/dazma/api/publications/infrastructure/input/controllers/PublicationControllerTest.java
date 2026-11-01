package co.dazma.api.publications.infrastructure.input.controllers;

import co.dazma.api.shared.infrastructure.security.CurrentUser;
import co.dazma.api.publications.domain.PublicationStatus;
import co.dazma.api.publications.domain.PublicationType;
import co.dazma.api.publications.infrastructure.input.mapper.PublicationWebMapper;
import co.dazma.api.publications.services.input.dto.CreatePublicationInputDto;
import co.dazma.api.publications.services.input.dto.CreatePublicationOutputDto;
import co.dazma.api.publications.services.input.dto.GetPublicationOutputDto;
import co.dazma.api.publications.services.input.dto.GetPublicationPhotoOutputDto;
import co.dazma.api.publications.services.input.dto.ListPublicationsInputDto;
import co.dazma.api.publications.services.input.dto.ListPublicationsOutputDto;
import co.dazma.api.publications.services.input.dto.PublicationSummaryOutputDto;
import co.dazma.api.publications.domain.PublicationNotFoundException;
import co.dazma.api.publications.services.input.ports.CreatePublicationInputPort;
import co.dazma.api.publications.services.input.ports.GetPublicationInputPort;
import co.dazma.api.publications.services.input.ports.ListPublicationsInputPort;
import co.dazma.api.shared.infrastructure.input.errors.ApiProblemFactory;
import co.dazma.api.shared.infrastructure.input.errors.ErrorMessageResolver;
import co.dazma.api.shared.infrastructure.input.errors.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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

    private CreatePublicationInputPort createPublicationInputPort;
    private GetPublicationInputPort getPublicationInputPort;
    private ListPublicationsInputPort listPublicationsInputPort;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        createPublicationInputPort = mock(CreatePublicationInputPort.class);
        getPublicationInputPort = mock(GetPublicationInputPort.class);
        listPublicationsInputPort = mock(ListPublicationsInputPort.class);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(
                new ApiProblemFactory(new ErrorMessageResolver(messageSource))
        );

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new PublicationController(
                                createPublicationInputPort,
                                getPublicationInputPort,
                                listPublicationsInputPort,
                                Mappers.getMapper(PublicationWebMapper.class)
                        )
                )
                .setControllerAdvice(exceptionHandler)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        setAuthenticatedUser(true);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
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
                true,
                "Bicicleta",
                PublicationType.EXCHANGE,
                "Bicicleta de montaña en buen estado",
                "05615",
                900_000,
                List.of(photoId)
        );
        when(createPublicationInputPort.execute(expectedInput)).thenReturn(publication);

        mockMvc.perform(post("/publications/api/v1/publications")
                        .principal(() -> "firebase-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Bicicleta",
                                  "type": "EXCHANGE",
                                  "description": "Bicicleta de montaña en buen estado",
                                  "municipality": "05615",
                                  "price": 900000,
                                  "photoUploadIds": ["%s"]
                                }
                                """.formatted(photoId)))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "http://localhost/publications/api/v1/publications/" + publication.id()
                ))
                .andExpect(jsonPath("$.id").value(publication.id().toString()))
                .andExpect(jsonPath("$.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.createdAt").value(createdAt.toString()));

        verify(createPublicationInputPort).execute(expectedInput);
    }

    private void setAuthenticatedUser(boolean emailVerified) {
        CurrentUser authenticatedUser = new CurrentUser(
                "firebase-123",
                "ana@dazma.store",
                emailVerified
        );
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        authenticatedUser,
                        null,
                        List.of()
                )
        );
    }

    @Test
    void shouldRejectRequestWithoutPriceBeforeCallingUseCase() throws Exception {
        mockMvc.perform(post("/publications/api/v1/publications")
                        .principal(() -> "firebase-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestWithoutPrice()))
                .andExpect(status().isBadRequest());

        verify(createPublicationInputPort, never()).execute(any());
    }

    @Disabled("GlobalExceptionHandler todavía no maneja MethodArgumentNotValidException")
    @Test
    void shouldDescribeTheInvalidFieldsInTheProblemDetail() throws Exception {
        mockMvc.perform(post("/publications/api/v1/publications")
                        .principal(() -> "firebase-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestWithoutPrice()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PUBLICATION_REQUEST_BODY_INVALID"))
                .andExpect(jsonPath("$.fields[0]").value("price"))
                .andExpect(jsonPath("$.instance").value("/publications/api/v1/publications"));
    }

    private String requestWithoutPrice() {
        return """
                {
                  "name": "Bicicleta",
                  "type": "EXCHANGE",
                  "description": "Bicicleta de montaña en buen estado",
                  "municipality": "05615",
                  "photoUploadIds": ["%s"]
                }
                """.formatted(UUID.randomUUID());
    }

    @Test
    void shouldReturnPublicationById() throws Exception {
        UUID publicationId = UUID.randomUUID();
        UUID photoId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        when(getPublicationInputPort.execute(publicationId)).thenReturn(new GetPublicationOutputDto(
                publicationId,
                ownerId,
                "Ana",
                "Bicicleta",
                PublicationType.EXCHANGE,
                "Bicicleta de montaña en buen estado",
                "05615",
                PublicationStatus.PUBLISHED,
                Instant.parse("2026-10-20T20:00:00Z"),
                900_000,
                List.of(new GetPublicationPhotoOutputDto(
                        photoId,
                        java.net.URI.create("https://storage.example/signed"),
                        0
                ))
        ));

        mockMvc.perform(get("/publications/api/v1/publications/{publicationId}", publicationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(publicationId.toString()))
                .andExpect(jsonPath("$.type").value("EXCHANGE"))
                .andExpect(jsonPath("$.municipality").value("05615"))
                .andExpect(jsonPath("$.price").value(900_000))
                .andExpect(jsonPath("$.owner").value(ownerId.toString()))
                .andExpect(jsonPath("$.ownerName").value("Ana"))
                .andExpect(jsonPath("$.ownerId").doesNotExist())
                .andExpect(jsonPath("$.photos[0].id").value(photoId.toString()))
                .andExpect(jsonPath("$.photos[0].url").value("https://storage.example/signed"))
                .andExpect(jsonPath("$.photos[0].position").value(0));
    }

    @Test
    void shouldReturnNotFoundWhenPublicationDoesNotExist() throws Exception {
        UUID publicationId = UUID.randomUUID();
        when(getPublicationInputPort.execute(publicationId))
                .thenThrow(new PublicationNotFoundException(publicationId));

        mockMvc.perform(get("/publications/api/v1/publications/{publicationId}", publicationId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldListPublicationsWithDefaultPagingWhenNoParametersAreSent() throws Exception {
        when(listPublicationsInputPort.execute(new ListPublicationsInputDto(Optional.empty(), 0, 20)))
                .thenReturn(new ListPublicationsOutputDto(List.of(), 0, 20, false));

        mockMvc.perform(get("/publications/api/v1/publications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    void shouldListPublicationsOfAMunicipalityWithoutExposingTheSeller() throws Exception {
        UUID publicationId = UUID.randomUUID();
        when(listPublicationsInputPort.execute(new ListPublicationsInputDto(Optional.of("05615"), 1, 5)))
                .thenReturn(new ListPublicationsOutputDto(
                        List.of(new PublicationSummaryOutputDto(
                                publicationId,
                                "Bicicleta",
                                PublicationType.EXCHANGE,
                                "05615",
                                900_000,
                                java.net.URI.create("https://storage.example/cover"),
                                Instant.parse("2026-10-20T20:00:00Z")
                        )),
                        1,
                        5,
                        true
                ));

        mockMvc.perform(get("/publications/api/v1/publications")
                        .param("municipality", "05615")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(publicationId.toString()))
                .andExpect(jsonPath("$.items[0].name").value("Bicicleta"))
                .andExpect(jsonPath("$.items[0].type").value("EXCHANGE"))
                .andExpect(jsonPath("$.items[0].municipality").value("05615"))
                .andExpect(jsonPath("$.items[0].price").value(900_000))
                .andExpect(jsonPath("$.items[0].coverUrl").value("https://storage.example/cover"))
                .andExpect(jsonPath("$.items[0].owner").doesNotExist())
                .andExpect(jsonPath("$.items[0].ownerId").doesNotExist())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.hasNext").value(true));
    }

    @Test
    void shouldRejectAPageSizeAboveTheMaximum() throws Exception {
        when(listPublicationsInputPort.execute(new ListPublicationsInputDto(Optional.empty(), 0, 500)))
                .thenThrow(new co.dazma.api.publications.domain.InvalidPublicationException(
                        co.dazma.api.publications.domain.PublicationErrorCode.PAGE_SIZE_INVALID, 50));

        mockMvc.perform(get("/publications/api/v1/publications").param("size", "500"))
                .andExpect(status().isBadRequest());
    }
}
