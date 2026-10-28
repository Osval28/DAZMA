package co.dazma.api.publications.infrastructure.input.errors;

import co.dazma.api.publications.domain.InvalidPublicationException;
import co.dazma.api.publications.domain.PublicationErrorCode;
import co.dazma.api.publications.domain.PublicationNotFoundException;
import co.dazma.api.publications.domain.PublicationPhotoNotAvailableException;
import co.dazma.api.publications.domain.PublicationOwnerEmailNotVerifiedException;
import co.dazma.api.shared.infrastructure.input.errors.ApiProblemFactory;
import co.dazma.api.shared.infrastructure.input.errors.ErrorMessageResolver;
import co.dazma.api.shared.infrastructure.input.errors.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PublicationExceptionHandlingTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        ErrorMessageResolver messageResolver = new ErrorMessageResolver(messageSource);
        handler = new GlobalExceptionHandler(new ApiProblemFactory(messageResolver));

        request = new MockHttpServletRequest();
        request.setRequestURI("/publications/api/v1/publications");
        request.addPreferredLocale(Locale.forLanguageTag("es"));
    }

    @Test
    void shouldReturnBadRequestForAnInvalidPublication() {
        ResponseEntity<ProblemDetail> response = handler.handleInvalid(
                new InvalidPublicationException(PublicationErrorCode.NAME_REQUIRED),
                request
        );

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Solicitud inválida", response.getBody().getTitle());
        assertEquals("El nombre de la publicación es obligatorio.", response.getBody().getDetail());
        assertEquals("PUBLICATION_NAME_REQUIRED", response.getBody().getProperties().get("code"));
        assertEquals("/publications/api/v1/publications", response.getBody().getInstance().toString());
    }

    @Test
    void shouldReturnNotFoundForAnUnavailablePublication() {
        UUID publicationId = UUID.randomUUID();
        request.setRequestURI("/publications/api/v1/publications/" + publicationId);

        ResponseEntity<ProblemDetail> response = handler.handleNotFound(
                new PublicationNotFoundException(publicationId),
                request
        );

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Recurso no encontrado", response.getBody().getTitle());
        assertEquals("PUBLICATION_NOT_FOUND", response.getBody().getProperties().get("code"));
        assertEquals("/publications/api/v1/publications/" + publicationId, response.getBody().getInstance().toString());
    }

    @Test
    void shouldReturnForbiddenWhenUploadedPhotoIsNotAvailable() {
        UUID photoId = UUID.randomUUID();

        ResponseEntity<ProblemDetail> response = handler.handleNotAllowed(
                new PublicationPhotoNotAvailableException(photoId),
                request
        );

        assertEquals(403, response.getStatusCode().value());
        assertEquals("Operación no permitida", response.getBody().getTitle());
        assertEquals(
                "La fotografía " + photoId
                        + " no fue cargada o no pertenece al usuario autenticado.",
                response.getBody().getDetail()
        );
        assertEquals(
                "PUBLICATION_PHOTO_UPLOAD_NOT_AVAILABLE",
                response.getBody().getProperties().get("code")
        );
    }

    @Test
    void shouldReturnForbiddenWhenOwnerEmailIsNotVerified() {
        ResponseEntity<ProblemDetail> response = handler.handleNotAllowed(
                new PublicationOwnerEmailNotVerifiedException(),
                request
        );

        assertEquals(403, response.getStatusCode().value());
        assertEquals("Operación no permitida", response.getBody().getTitle());
        assertEquals(
                "Debes verificar tu correo electrónico antes de crear una publicación.",
                response.getBody().getDetail()
        );
        assertEquals(
                "PUBLICATION_OWNER_EMAIL_NOT_VERIFIED",
                response.getBody().getProperties().get("code")
        );
    }
}
