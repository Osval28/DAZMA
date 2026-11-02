package co.dazma.api.shared.infrastructure.input.errors;

import co.dazma.api.shared.domain.errors.DomainException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ErrorMessageResolver {

    private final MessageSource messageSource;

    public ErrorMessageResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String resolve(DomainException exception, Locale locale) {
        return resolve(exception.messageKey(), locale, exception.arguments());
    }

    public String resolve(String messageKey, Locale locale, Object... arguments) {
        Locale resolvedLocale = locale == null ? Locale.ROOT : locale;
        Object[] resolvedArguments = arguments == null ? new Object[0] : arguments;

        return messageSource.getMessage(
                messageKey,
                resolvedArguments,
                resolvedLocale
        );
    }
}
