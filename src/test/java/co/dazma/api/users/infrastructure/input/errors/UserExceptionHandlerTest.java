package co.dazma.api.users.infrastructure.input.errors;

import co.dazma.api.shared.infrastructure.input.errors.ApiProblemFactory;
import co.dazma.api.shared.infrastructure.input.errors.ErrorMessageResolver;
import co.dazma.api.users.domain.MissingRequiredFieldException;
import co.dazma.api.users.domain.UserAlreadyRegisteredException;
import co.dazma.api.users.domain.UserAlreadySuspendedException;
import co.dazma.api.users.domain.UserCannotOperateException;
import co.dazma.api.users.domain.UserNotSuspendedException;
import co.dazma.api.users.domain.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserExceptionHandlerTest {

    private UserExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        ErrorMessageResolver messageResolver = new ErrorMessageResolver(messageSource);
        handler = new UserExceptionHandler(new ApiProblemFactory(messageResolver));

        request = new MockHttpServletRequest();
        request.setRequestURI("/api/users");
        request.addPreferredLocale(Locale.forLanguageTag("es"));
    }

    @Test
    void shouldReturnBadRequestWhenARequiredFieldIsMissing() {
        ResponseEntity<ProblemDetail> response = handler.handleMissingRequiredField(
                new MissingRequiredFieldException("phone"),
                request
        );

        assertProblem(
                response,
                400,
                "Solicitud inválida",
                "El campo phone es obligatorio.",
                "USER_REQUIRED_FIELD_MISSING"
        );
    }

    @Test
    void shouldReturnConflictWhenTheUserIsAlreadyRegistered() {
        ResponseEntity<ProblemDetail> response = handler.handleAlreadyRegistered(
                new UserAlreadyRegisteredException(),
                request
        );

        assertProblem(
                response,
                409,
                "Conflicto con el estado actual",
                "El usuario ya tiene un perfil registrado.",
                "USER_ALREADY_REGISTERED"
        );
    }

    @Test
    void shouldReturnConflictWhenTheUserIsAlreadySuspended() {
        ResponseEntity<ProblemDetail> response = handler.handleAlreadySuspended(
                new UserAlreadySuspendedException(),
                request
        );

        assertProblem(
                response,
                409,
                "Conflicto con el estado actual",
                "El usuario ya está suspendido.",
                "USER_ALREADY_SUSPENDED"
        );
    }

    @Test
    void shouldReturnConflictWhenTheUserIsNotSuspended() {
        ResponseEntity<ProblemDetail> response = handler.handleNotSuspended(
                new UserNotSuspendedException(),
                request
        );

        assertProblem(
                response,
                409,
                "Conflicto con el estado actual",
                "El usuario no está suspendido.",
                "USER_NOT_SUSPENDED"
        );
    }

    @Test
    void shouldReturnNotFoundWhenTheAuthenticatedUserHasNoProfile() {
        ResponseEntity<ProblemDetail> response = handler.handleNotFound(
                new UserNotFoundException(),
                request
        );

        assertProblem(
                response,
                404,
                "Recurso no encontrado",
                "No existe un perfil registrado para el usuario autenticado.",
                "USER_NOT_FOUND"
        );
    }

    @Test
    void shouldReturnForbiddenWhenTheUserCannotOperate() {
        ResponseEntity<ProblemDetail> response = handler.handleCannotOperate(
                new UserCannotOperateException(),
                request
        );

        assertProblem(
                response,
                403,
                "Operación no permitida",
                "El usuario autenticado está suspendido y no puede realizar esta operación.",
                "USER_CANNOT_OPERATE"
        );
    }

    private void assertProblem(
            ResponseEntity<ProblemDetail> response,
            int status,
            String title,
            String detail,
            String code
    ) {
        ProblemDetail problem = response.getBody();

        assertEquals(status, response.getStatusCode().value());
        assertNotNull(problem);
        assertEquals(title, problem.getTitle());
        assertEquals(detail, problem.getDetail());
        assertEquals(code, problem.getProperties().get("code"));
        assertEquals("/api/users", problem.getInstance().toString());
    }
}
