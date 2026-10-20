package co.dazma.api.users.infrastructure.input.errors;

import co.dazma.api.shared.domain.errors.DomainException;
import co.dazma.api.shared.infrastructure.input.errors.ApiProblemFactory;
import co.dazma.api.users.domain.MissingRequiredFieldException;
import co.dazma.api.users.domain.UserAlreadyRegisteredException;
import co.dazma.api.users.domain.UserAlreadySuspendedException;
import co.dazma.api.users.domain.UserNotSuspendedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class UserExceptionHandler {

    private final ApiProblemFactory problemFactory;

    public UserExceptionHandler(ApiProblemFactory problemFactory) {
        this.problemFactory = problemFactory;
    }

    @ExceptionHandler(MissingRequiredFieldException.class)
    public ResponseEntity<ProblemDetail> handleMissingRequiredField(
            MissingRequiredFieldException exception,
            HttpServletRequest request
    ) {
        return createResponse(
                exception,
                HttpStatus.BAD_REQUEST,
                "error.title.bad-request",
                request
        );
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<ProblemDetail> handleAlreadyRegistered(
            UserAlreadyRegisteredException exception,
            HttpServletRequest request
    ) {
        return createConflictResponse(exception, request);
    }

    @ExceptionHandler(UserAlreadySuspendedException.class)
    public ResponseEntity<ProblemDetail> handleAlreadySuspended(
            UserAlreadySuspendedException exception,
            HttpServletRequest request
    ) {
        return createConflictResponse(exception, request);
    }

    @ExceptionHandler(UserNotSuspendedException.class)
    public ResponseEntity<ProblemDetail> handleNotSuspended(
            UserNotSuspendedException exception,
            HttpServletRequest request
    ) {
        return createConflictResponse(exception, request);
    }

    private ResponseEntity<ProblemDetail> createConflictResponse(
            DomainException exception,
            HttpServletRequest request
    ) {
        return createResponse(
                exception,
                HttpStatus.CONFLICT,
                "error.title.conflict",
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
