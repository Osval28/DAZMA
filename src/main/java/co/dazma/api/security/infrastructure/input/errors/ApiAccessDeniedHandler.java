package co.dazma.api.security.infrastructure.input.errors;

import co.dazma.api.security.domain.SecurityErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    private final SecurityProblemWriter problemWriter;

    public ApiAccessDeniedHandler(SecurityProblemWriter problemWriter) {
        this.problemWriter = problemWriter;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exception
    ) throws IOException, ServletException {
        problemWriter.write(
                request,
                response,
                SecurityErrorCode.ACCESS_DENIED,
                HttpStatus.FORBIDDEN,
                "error.title.forbidden"
        );
    }
}
