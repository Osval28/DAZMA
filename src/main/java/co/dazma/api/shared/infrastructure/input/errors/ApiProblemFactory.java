package co.dazma.api.shared.infrastructure.input.errors;

import co.dazma.api.shared.domain.errors.DomainException;
import co.dazma.api.shared.domain.errors.ErrorCode;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Locale;
import java.util.Objects;

@Component
public class ApiProblemFactory {

    private final ErrorMessageResolver messageResolver;

    public ApiProblemFactory(ErrorMessageResolver messageResolver) {
        this.messageResolver = Objects.requireNonNull(
                messageResolver,
                "El resolvedor de mensajes es obligatorio"
        );
    }

    public ProblemDetail create(
            DomainException exception,
            HttpStatusCode status,
            String titleMessageKey,
            URI instance,
            Locale locale
    ) {
        Objects.requireNonNull(exception, "La excepción de dominio es obligatoria");

        return create(
                exception.errorCode(),
                exception.arguments(),
                status,
                titleMessageKey,
                instance,
                locale
        );
    }

    public ProblemDetail create(
            ErrorCode errorCode,
            Object[] arguments,
            HttpStatusCode status,
            String titleMessageKey,
            URI instance,
            Locale locale
    ) {
        Objects.requireNonNull(errorCode, "El código del error es obligatorio");
        Objects.requireNonNull(status, "El estado HTTP es obligatorio");
        Objects.requireNonNull(titleMessageKey, "La clave del título es obligatoria");
        Objects.requireNonNull(instance, "La dirección de la solicitud es obligatoria");

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                status,
                messageResolver.resolve(errorCode.messageKey(), locale, arguments)
        );

        problem.setTitle(messageResolver.resolve(titleMessageKey, locale));
        problem.setInstance(instance);
        problem.setProperty("code", errorCode.code());

        return problem;
    }
}
