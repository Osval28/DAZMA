package co.dazma.api.security.infrastructure.input.filters;

import co.dazma.api.security.domain.SecurityErrorCode;
import co.dazma.api.security.infrastructure.input.errors.SecurityProblemWriter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class OriginSecretFilterTest {

    private SecurityProblemWriter problemWriter;
    private OriginSecretFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        problemWriter = mock(SecurityProblemWriter.class);
        filter = new OriginSecretFilter(Set.of("current-secret", "previous-secret"), problemWriter);
        request = new MockHttpServletRequest("GET", "/publications/api/v1/publications");
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
    }

    @Test
    void shouldLetThroughARequestWithTheCurrentSecret() throws Exception {
        request.addHeader(OriginSecretFilter.HEADER, "current-secret");

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(problemWriter);
    }

    @Test
    void shouldLetThroughARequestWithThePreviousSecretWhileRotating() throws Exception {
        request.addHeader(OriginSecretFilter.HEADER, "previous-secret");

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldRejectARequestWithoutTheSecret() throws Exception {
        filter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        verify(problemWriter).write(
                request,
                response,
                SecurityErrorCode.ORIGIN_NOT_ALLOWED,
                HttpStatus.FORBIDDEN,
                "error.title.forbidden"
        );
    }

    @Test
    void shouldRejectARequestWithAWrongSecret() throws Exception {
        request.addHeader(OriginSecretFilter.HEADER, "guessed-secret");

        filter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldLetTheHealthCheckThroughWithoutTheSecret() throws Exception {
        request.setRequestURI("/actuator/health");

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(problemWriter);
    }
}
