package co.dazma.api.shared.infrastructure.input.errors;

import co.dazma.api.publications.domain.PublicationNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiProblemFactoryTest {

    @Test
    void shouldCreateAProblemDetailFromADomainException() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        ErrorMessageResolver messageResolver = new ErrorMessageResolver(messageSource);
        ApiProblemFactory factory = new ApiProblemFactory(messageResolver);
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
}
