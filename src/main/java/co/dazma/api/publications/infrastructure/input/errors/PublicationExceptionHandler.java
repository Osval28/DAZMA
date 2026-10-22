package co.dazma.api.publications.infrastructure.input.errors;

import co.dazma.api.publications.domain.InvalidPublicationException;
import co.dazma.api.publications.domain.PublicationNotFoundException;
import co.dazma.api.publications.domain.PublicationPhotoNotAvailableException;
import co.dazma.api.publications.infrastructure.input.controllers.PublicationController;
import co.dazma.api.shared.domain.errors.DomainException;
import co.dazma.api.shared.infrastructure.input.errors.ApiProblemFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice(basePackageClasses = PublicationController.class)
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

    @ExceptionHandler(PublicationPhotoNotAvailableException.class)
    public ResponseEntity<ProblemDetail> handlePhotoNotAvailable(
            PublicationPhotoNotAvailableException exception,
            HttpServletRequest request
    ) {
        return createResponse(
                exception,
                HttpStatus.CONFLICT,
                "error.title.conflict",
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        ProblemDetail problem = problemFactory.create(
                PublicationRequestErrorCode.REQUEST_BODY_INVALID,
                new Object[0],
                HttpStatus.BAD_REQUEST,
                "error.title.bad-request",
                URI.create(request.getRequestURI()),
                request.getLocale()
        );
        problem.setProperty("fields", exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getField)
                .distinct()
                .sorted()
                .toList());

        return ResponseEntity.badRequest().body(problem);
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
