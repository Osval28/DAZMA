package co.dazma.api.shared.infrastructure.input.errors;

import co.dazma.api.shared.domain.errors.DazmaException;
import co.dazma.api.shared.domain.errors.ErrorCode;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Locale;

@Component
public class ApiProblemFactory {

    private final ErrorMessageResolver messageResolver;

    public ApiProblemFactory(ErrorMessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    public ProblemDetail create(
            DazmaException exception,
            HttpStatusCode status,
            String titleMessageKey,
            URI instance,
            Locale locale
    ) {
        return create(
                exception.errorCode(),
                exception.arguments(),
                status,
                titleMessageKey,
                instance,
                locale
        );
    }

    public ProblemDetail createWithGenericDetail(
            DazmaException exception,
            HttpStatusCode status,
            String titleMessageKey,
            String detailMessageKey,
            URI instance,
            Locale locale
    ) {
        return build(
                exception.code(),
                messageResolver.resolve(detailMessageKey, locale),
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
        return build(
                errorCode.code(),
                messageResolver.resolve(errorCode.messageKey(), locale, arguments),
                status,
                titleMessageKey,
                instance,
                locale
        );
    }

    private ProblemDetail build(
            String code,
            String detail,
            HttpStatusCode status,
            String titleMessageKey,
            URI instance,
            Locale locale
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(messageResolver.resolve(titleMessageKey, locale));
        problem.setInstance(instance);
        problem.setProperty("code", code);
        return problem;
    }
}
