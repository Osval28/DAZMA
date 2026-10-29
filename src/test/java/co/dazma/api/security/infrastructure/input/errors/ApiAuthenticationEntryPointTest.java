package co.dazma.api.security.infrastructure.input.errors;

import co.dazma.api.security.domain.SecurityErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ApiAuthenticationEntryPointTest {

    private final SecurityProblemWriter problemWriter = mock(SecurityProblemWriter.class);
    private final ApiAuthenticationEntryPoint entryPoint = new ApiAuthenticationEntryPoint(problemWriter);
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/users/api/v1/users/me");
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @Test
    void shouldAskForAuthenticationWhenThereIsNoSession() throws Exception {
        entryPoint.commence(request, response, new InsufficientAuthenticationException("anonymous"));

        verify(problemWriter).write(
                request,
                response,
                SecurityErrorCode.AUTHENTICATION_REQUIRED,
                HttpStatus.UNAUTHORIZED,
                "error.title.unauthorized"
        );
    }

    @Test
    void shouldReportAnInvalidSessionWhenTheFilterRejectedTheCookie() throws Exception {
        request.setAttribute(ApiAuthenticationEntryPoint.INVALID_SESSION_ATTRIBUTE, Boolean.TRUE);

        entryPoint.commence(request, response, new InsufficientAuthenticationException("anonymous"));

        verify(problemWriter).write(
                request,
                response,
                SecurityErrorCode.IDENTITY_TOKEN_INVALID,
                HttpStatus.UNAUTHORIZED,
                "error.title.unauthorized"
        );
    }
}
