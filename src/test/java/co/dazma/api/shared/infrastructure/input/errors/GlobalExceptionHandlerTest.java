package co.dazma.api.shared.infrastructure.input.errors;

import co.dazma.api.shared.domain.errors.DomainConflictException;
import co.dazma.api.shared.domain.errors.DomainInvalidException;
import co.dazma.api.shared.domain.errors.DomainNotAllowedException;
import co.dazma.api.shared.domain.errors.DomainNotFoundException;
import co.dazma.api.shared.domain.errors.DomainUnauthenticatedException;
import co.dazma.api.shared.domain.errors.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private static final Locale LOCALE = Locale.forLanguageTag("es");

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("error.title.bad-request", LOCALE, "Solicitud inválida");
        messageSource.addMessage("error.title.forbidden", LOCALE, "Operación no permitida");
        messageSource.addMessage("error.title.unauthorized", LOCALE, "Autenticación requerida");
        messageSource.addMessage("error.title.not-found", LOCALE, "Recurso no encontrado");
        messageSource.addMessage("error.title.conflict", LOCALE, "Conflicto con el estado actual");
        messageSource.addMessage("test.error", LOCALE, "Detalle de prueba {0}.");
        handler = new GlobalExceptionHandler(
                new ApiProblemFactory(new ErrorMessageResolver(messageSource))
        );

        request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        request.addPreferredLocale(LOCALE);
    }

    @Test
    void shouldMapAnInvalidExceptionToBadRequest() {
        ResponseEntity<ProblemDetail> response = handler.handleInvalid(
                new TestInvalidException(),
                request
        );

        assertProblem(response, 400, "Solicitud inválida");
    }

    @Test
    void shouldMapAnUnauthenticatedExceptionToUnauthorized() {
        ResponseEntity<ProblemDetail> response = handler.handleUnauthenticated(
                new TestUnauthenticatedException(),
                request
        );

        assertProblem(response, 401, "Autenticación requerida");
    }

    @Test
    void shouldMapANotAllowedExceptionToForbidden() {
        ResponseEntity<ProblemDetail> response = handler.handleNotAllowed(
                new TestNotAllowedException(),
                request
        );

        assertProblem(response, 403, "Operación no permitida");
    }

    @Test
    void shouldMapANotFoundExceptionToNotFound() {
        ResponseEntity<ProblemDetail> response = handler.handleNotFound(
                new TestNotFoundException(),
                request
        );

        assertProblem(response, 404, "Recurso no encontrado");
    }

    @Test
    void shouldMapAConflictExceptionToConflict() {
        ResponseEntity<ProblemDetail> response = handler.handleConflict(
                new TestConflictException(),
                request
        );

        assertProblem(response, 409, "Conflicto con el estado actual");
    }

    private void assertProblem(ResponseEntity<ProblemDetail> response, int status, String title) {
        ProblemDetail problem = response.getBody();

        assertEquals(status, response.getStatusCode().value());
        assertNotNull(problem);
        assertEquals(status, problem.getStatus());
        assertEquals(title, problem.getTitle());
        assertEquals("Detalle de prueba 7.", problem.getDetail());
        assertEquals("TEST_ERROR", problem.getProperties().get("code"));
        assertEquals("/api/test", problem.getInstance().toString());
    }

    private enum TestErrorCode implements ErrorCode {
        TEST_ERROR;

        @Override
        public String code() {
            return "TEST_ERROR";
        }

        @Override
        public String messageKey() {
            return "test.error";
        }
    }

    private static final class TestInvalidException extends DomainInvalidException {
        TestInvalidException() {
            super(TestErrorCode.TEST_ERROR, 7);
        }
    }

    private static final class TestUnauthenticatedException extends DomainUnauthenticatedException {
        TestUnauthenticatedException() {
            super(TestErrorCode.TEST_ERROR, 7);
        }
    }

    private static final class TestNotAllowedException extends DomainNotAllowedException {
        TestNotAllowedException() {
            super(TestErrorCode.TEST_ERROR, 7);
        }
    }

    private static final class TestNotFoundException extends DomainNotFoundException {
        TestNotFoundException() {
            super(TestErrorCode.TEST_ERROR, 7);
        }
    }

    private static final class TestConflictException extends DomainConflictException {
        TestConflictException() {
            super(TestErrorCode.TEST_ERROR, 7);
        }
    }
}
