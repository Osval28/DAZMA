package co.dazma.api.shared.infrastructure.input.errors;

import co.dazma.api.publications.domain.PublicationNotFoundException;
import co.dazma.api.shared.infrastructure.parameters.InvalidParameterException;
import co.dazma.api.shared.infrastructure.parameters.ParameterErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ApiProblemFactoryTest {

    private final ApiProblemFactory factory = new ApiProblemFactory(new ErrorMessageResolver(messages()));

    @Test
    void shouldCreateAProblemDetailFromADomainException() {
        UUID publicationId = UUID.randomUUID();
        URI instance = URI.create("/publications/api/v1/publications/" + publicationId);

        ProblemDetail problem = factory.create(
                new PublicationNotFoundException(publicationId),
                HttpStatus.NOT_FOUND,
                "error.title.not-found",
                instance,
                Locale.forLanguageTag("es")
        );

        assertEquals(404, problem.getStatus());
        assertEquals("Recurso no encontrado", problem.getTitle());
        assertEquals(
                "No existe una publicación disponible con el identificador " + publicationId + ".",
                problem.getDetail()
        );
        assertEquals("PUBLICATION_NOT_FOUND", problem.getProperties().get("code"));
        assertEquals(instance, problem.getInstance());
    }

    @Test
    void shouldHideTheInternalDetailOfATechnicalException() {
        URI instance = URI.create("/publications/api/v1/photos/upload-authorizations");

        ProblemDetail problem = factory.createWithGenericDetail(
                new InvalidParameterException(ParameterErrorCode.VALUE_NOT_POSITIVE, () -> "dazma.test.parameter"),
                HttpStatus.INTERNAL_SERVER_ERROR,
                "error.title.internal",
                "error.detail.internal",
                instance,
                Locale.forLanguageTag("es")
        );

        assertEquals(500, problem.getStatus());
        assertEquals("Error interno del servidor", problem.getTitle());
        assertEquals("Ocurrió un error inesperado. Inténtalo de nuevo más tarde.", problem.getDetail());
        assertFalse(problem.getDetail().contains("dazma.test.parameter"));
        assertEquals("PARAMETER_VALUE_NOT_POSITIVE", problem.getProperties().get("code"));
    }

    private static ResourceBundleMessageSource messages() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return messageSource;
    }
}
