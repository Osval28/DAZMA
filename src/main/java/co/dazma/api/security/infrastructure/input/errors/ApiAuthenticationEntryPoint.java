package co.dazma.api.security.infrastructure.input.errors;

import co.dazma.api.security.domain.SecurityErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public static final String INVALID_SESSION_ATTRIBUTE =
            ApiAuthenticationEntryPoint.class.getName() + ".INVALID_SESSION";

    private final SecurityProblemWriter problemWriter;

    public ApiAuthenticationEntryPoint(SecurityProblemWriter problemWriter) {
        this.problemWriter = Objects.requireNonNull(
                problemWriter,
                "El escritor de errores de seguridad es obligatorio"
        );
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        problemWriter.write(
                request,
                response,
                errorCodeFor(request),
                HttpStatus.UNAUTHORIZED,
                "error.title.unauthorized"
        );
    }

    private static SecurityErrorCode errorCodeFor(HttpServletRequest request) {
        if (Boolean.TRUE.equals(request.getAttribute(INVALID_SESSION_ATTRIBUTE))) {
            return SecurityErrorCode.IDENTITY_TOKEN_INVALID;
        }
        return SecurityErrorCode.AUTHENTICATION_REQUIRED;
    }
}
