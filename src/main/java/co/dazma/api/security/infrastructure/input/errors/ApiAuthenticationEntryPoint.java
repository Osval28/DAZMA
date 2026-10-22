package co.dazma.api.security.infrastructure.input.errors;

import co.dazma.api.security.domain.InvalidIdentityTokenException;
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
                SecurityErrorCode.AUTHENTICATION_REQUIRED,
                HttpStatus.UNAUTHORIZED,
                "error.title.unauthorized"
        );
    }

    public void commenceInvalidToken(
            HttpServletRequest request,
            HttpServletResponse response,
            InvalidIdentityTokenException exception
    ) throws IOException {
        Objects.requireNonNull(exception, "La excepción del token es obligatoria");

        problemWriter.write(
                request,
                response,
                SecurityErrorCode.IDENTITY_TOKEN_INVALID,
                HttpStatus.UNAUTHORIZED,
                "error.title.unauthorized"
        );
    }
}
