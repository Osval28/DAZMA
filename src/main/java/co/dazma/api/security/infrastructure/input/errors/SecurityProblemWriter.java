package co.dazma.api.security.infrastructure.input.errors;

import co.dazma.api.security.domain.SecurityErrorCode;
import co.dazma.api.shared.infrastructure.input.errors.ApiProblemFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
public class SecurityProblemWriter {

    private final ApiProblemFactory problemFactory;
    private final ObjectMapper objectMapper;

    public SecurityProblemWriter(
            ApiProblemFactory problemFactory,
            ObjectMapper objectMapper
    ) {
        this.problemFactory = Objects.requireNonNull(
                problemFactory,
                "La fábrica de respuestas de error es obligatoria"
        );
        this.objectMapper = Objects.requireNonNull(
                objectMapper,
                "El serializador JSON es obligatorio"
        );
    }

    public void write(
            HttpServletRequest request,
            HttpServletResponse response,
            SecurityErrorCode errorCode,
            HttpStatus status,
            String titleMessageKey
    ) throws IOException {
        ProblemDetail problem = problemFactory.create(
                errorCode,
                new Object[0],
                status,
                titleMessageKey,
                URI.create(request.getRequestURI()),
                request.getLocale()
        );

        response.setStatus(status.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problem);
    }
}
