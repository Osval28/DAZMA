package co.dazma.api.shared.infrastructure.input.errors;

import co.dazma.api.shared.domain.errors.DazmaException;
import co.dazma.api.shared.domain.errors.DomainConflictException;
import co.dazma.api.shared.domain.errors.DomainException;
import co.dazma.api.shared.domain.errors.DomainInvalidException;
import co.dazma.api.shared.domain.errors.DomainNotAllowedException;
import co.dazma.api.shared.domain.errors.DomainNotFoundException;
import co.dazma.api.shared.domain.errors.DomainUnauthenticatedException;
import co.dazma.api.shared.domain.errors.TechnicalException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.Arrays;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
    ) {
        return createResponse(exception, HttpStatus.BAD_REQUEST, "error.title.bad-request", request);
    }

    @ExceptionHandler(DomainUnauthenticatedException.class)
    public ResponseEntity<ProblemDetail> handleUnauthenticated(
            DomainUnauthenticatedException exception,
            HttpServletRequest request
    ) {
        return createResponse(exception, HttpStatus.UNAUTHORIZED, "error.title.unauthorized", request);
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
        return createResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR, "error.title.internal", request);
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<ProblemDetail> handleTechnicalException(
            TechnicalException exception,
            HttpServletRequest request
    ) {
        log.atError()
                .setCause(exception)
                .addKeyValue("event.name", "technical.operation.failed")
                .addKeyValue("error.code", exception.code())
                .addKeyValue("error.arguments", Arrays.toString(exception.arguments()))
                .addKeyValue("http.route", request.getRequestURI())
                .addKeyValue("http.response.status_code", HttpStatus.INTERNAL_SERVER_ERROR.value())
                .log("Falla técnica");
        ProblemDetail problem = problemFactory.createWithGenericDetail(
                exception,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "error.title.internal",
                "error.detail.internal",
                URI.create(request.getRequestURI()),
                request.getLocale()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    private ResponseEntity<ProblemDetail> createResponse(
            DazmaException exception,
            HttpStatus status,
            String titleMessageKey,
            HttpServletRequest request
    ) {
        LoggingEventBuilder event = status.is5xxServerError() ? log.atError() : log.atWarn();
        event
                .setCause(status.is5xxServerError() ? exception : null)
                .addKeyValue("event.name", "domain.operation.rejected")
                .addKeyValue("error.code", exception.code())
                .addKeyValue("http.route", request.getRequestURI())
                .addKeyValue("http.response.status_code", status.value())
                .log("Operación de dominio rechazada");
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
