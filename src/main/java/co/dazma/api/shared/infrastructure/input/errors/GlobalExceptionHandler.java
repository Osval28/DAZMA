package co.dazma.api.shared.infrastructure.input.errors;

import co.dazma.api.shared.domain.errors.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ApiProblemFactory problemFactory;

    public GlobalExceptionHandler(ApiProblemFactory problemFactory) {
        this.problemFactory = problemFactory;
    }

    @ExceptionHandler(DomainNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(
            DomainNotFoundException exception,
            HttpServletRequest request
    ) {
        return createResponse(exception, HttpStatus.NOT_FOUND, "error.title.not-found", request);
    }

    @ExceptionHandler(DomainNotAllowedException.class)
    public ResponseEntity<ProblemDetail> handleNotAllowed(
            DomainNotAllowedException exception,
            HttpServletRequest request
    ) {
        return createResponse(exception, HttpStatus.FORBIDDEN, "error.title.forbidden", request);
    }

    @ExceptionHandler(DomainInvalidException.class)
    public ResponseEntity<ProblemDetail> handleInvalid(
            DomainInvalidException exception,
            HttpServletRequest request
    ){
        return createResponse(exception, HttpStatus.BAD_REQUEST, "error.title.bad-request", request);
    }

    @ExceptionHandler(DomainConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflict(
            DomainConflictException exception,
            HttpServletRequest request
    ) {
        return createResponse(exception, HttpStatus.CONFLICT, "error.title.conflict", request);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleException(
            DomainException exception,
            HttpServletRequest request
    ) {
        return createResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR, "error.title.internal-server-error", request);
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
