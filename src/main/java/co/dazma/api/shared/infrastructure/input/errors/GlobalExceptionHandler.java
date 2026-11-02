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
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.Arrays;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String INTERNAL_ERROR_TITLE = "error.title.internal";
    private static final String INTERNAL_ERROR_DETAIL = "error.detail.internal";

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
        logInternalError(exception);
        return createResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR_TITLE, request);
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<ProblemDetail> handleTechnicalException(
            TechnicalException exception,
            HttpServletRequest request
    ) {
        logInternalError(exception);
        ProblemDetail problem = problemFactory.createWithGenericDetail(
                exception,
                HttpStatus.INTERNAL_SERVER_ERROR,
                INTERNAL_ERROR_TITLE,
                INTERNAL_ERROR_DETAIL,
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
        ProblemDetail problem = problemFactory.create(
                exception,
                status,
                titleMessageKey,
                URI.create(request.getRequestURI()),
                request.getLocale()
        );
        return ResponseEntity.status(status).body(problem);
    }

    private static void logInternalError(DazmaException exception) {
        LOGGER.error(
                "Error interno {} con argumentos {}",
                exception.code(),
                Arrays.toString(exception.arguments()),
                exception
        );
    }
}
