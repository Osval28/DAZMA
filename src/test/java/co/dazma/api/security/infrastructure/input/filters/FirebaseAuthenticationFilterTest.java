package co.dazma.api.security.infrastructure.input.filters;

import co.dazma.api.security.domain.InvalidIdentityTokenException;
import co.dazma.api.security.infrastructure.input.errors.ApiAuthenticationEntryPoint;
import co.dazma.api.security.services.input.ports.AuthenticateIdentityUseCase;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class FirebaseAuthenticationFilterTest {

    private AuthenticateIdentityUseCase authenticateIdentityUseCase;
    private ApiAuthenticationEntryPoint authenticationEntryPoint;
    private FirebaseAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        authenticateIdentityUseCase = mock(AuthenticateIdentityUseCase.class);
        authenticationEntryPoint = mock(ApiAuthenticationEntryPoint.class);
        filter = new FirebaseAuthenticationFilter(
                authenticateIdentityUseCase,
                authenticationEntryPoint
        );
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldCreateAuthenticationWhenBearerTokenIsValid() throws Exception {
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid-token");
        when(authenticateIdentityUseCase.authenticate("valid-token"))
                .thenReturn("firebase-user-123");

        filter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());
        assertEquals("firebase-user-123", authentication.getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenAuthorizationHeaderIsMissing() throws Exception {
        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(authenticateIdentityUseCase);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldReturnUnauthorizedWhenBearerTokenIsInvalid() throws Exception {
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid-token");
        InvalidIdentityTokenException exception = new InvalidIdentityTokenException();
        when(authenticateIdentityUseCase.authenticate("invalid-token"))
                .thenThrow(exception);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationEntryPoint).commenceInvalidToken(request, response, exception);
        verify(filterChain, never()).doFilter(request, response);
    }
}
