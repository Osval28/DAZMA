package co.dazma.api.shared.infrastructure.input.errors;

import co.dazma.api.publications.domain.PublicationNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorMessageResolverTest {

    @Test
    void shouldResolveTheMessageAndReplaceItsArguments() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        ErrorMessageResolver resolver = new ErrorMessageResolver(messageSource);
        UUID publicationId = UUID.randomUUID();

        String message = resolver.resolve(
                new PublicationNotFoundException(publicationId),
                Locale.forLanguageTag("es")
        );

        assertEquals(
                "No existe una publicación disponible con el identificador " + publicationId + ".",
                message
        );
    }
}
