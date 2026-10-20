package co.dazma.api.publications.infrastructure.input.errors;

import co.dazma.api.publications.domain.InvalidPublicationException;
import co.dazma.api.publications.domain.PublicationNotFoundException;
import co.dazma.api.shared.domain.errors.DomainException;
import co.dazma.api.shared.infrastructure.input.errors.ApiProblemFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class PublicationExceptionHandler {

    private final ApiProblemFactory problemFactory;

    public PublicationExceptionHandler(ApiProblemFactory problemFactory) {
        this.problemFactory = problemFactory;
    }

    @ExceptionHandler(InvalidPublicationException.class)
    public ResponseEntity<ProblemDetail> handleInvalidPublication(
            InvalidPublicationException exception,
            HttpServletRequest request
    ) {
        return createResponse(
                exception,
                HttpStatus.BAD_REQUEST,
                "error.title.bad-request",
                request
        );
    }

    @ExceptionHandler(PublicationNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlePublicationNotFound(
            PublicationNotFoundException exception,
            HttpServletRequest request
    ) {
        return createResponse(
                exception,
                HttpStatus.NOT_FOUND,
                "error.title.not-found",
                request
        );
    }

    private ResponseEntity<ProblemDetail> createResponse(
            DomainException exception,
            HttpStatus status,
            String titleMessageKey,
            HttpServletRequest request
    ) {
        ProblemDetail problem = problemFactory.create(
                exception,
                status,
                titleMessageKey,
                URI.create(request.getRequestURI()),
                request.getLocale()
        );

        return ResponseEntity.status(status).body(problem);
    }
}
