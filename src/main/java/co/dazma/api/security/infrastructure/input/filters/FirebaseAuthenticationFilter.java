package co.dazma.api.security.infrastructure.input.filters;

import co.dazma.api.shared.infrastructure.security.CurrentUser;
import co.dazma.api.security.domain.InvalidIdentityTokenException;
import co.dazma.api.security.infrastructure.input.controllers.SessionController;
import co.dazma.api.security.infrastructure.input.cookies.SessionCookies;
import co.dazma.api.security.infrastructure.input.errors.ApiAuthenticationEntryPoint;
import co.dazma.api.security.services.input.dto.AuthenticateIdentityInputDto;
import co.dazma.api.security.services.input.dto.AuthenticateIdentityOutputDto;
import co.dazma.api.security.services.input.ports.AuthenticateIdentityInputPort;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private static final Set<String> READ_ONLY_METHODS = Set.of("GET", "HEAD", "OPTIONS");

    private final AuthenticateIdentityInputPort authenticateIdentityInputPort;
    private final ApiAuthenticationEntryPoint authenticationEntryPoint;

    public FirebaseAuthenticationFilter(
            AuthenticateIdentityInputPort authenticateIdentityInputPort,
            ApiAuthenticationEntryPoint authenticationEntryPoint
    ) {
        this.authenticateIdentityInputPort = Objects.requireNonNull(
                authenticateIdentityInputPort,
                "El puerto de entrada de autenticación es obligatorio"
        );
        this.authenticationEntryPoint = Objects.requireNonNull(
                authenticationEntryPoint,
                "El punto de entrada de autenticación es obligatorio"
        );
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith(SessionController.PATH);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Cookie sessionCookie = WebUtils.getCookie(request, SessionCookies.NAME);

        if (sessionCookie == null || sessionCookie.getValue().isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = sessionCookie.getValue();

        try {
            AuthenticateIdentityOutputDto identity = authenticateIdentityInputPort.execute(
                    new AuthenticateIdentityInputDto(token, changesState(request))
            );
            CurrentUser principal = new CurrentUser(
                    identity.uid(),
                    identity.email(),
                    identity.emailVerified()
            );

            UsernamePasswordAuthenticationToken authentication =
                    UsernamePasswordAuthenticationToken.authenticated(principal, null, List.of());
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (InvalidIdentityTokenException exception) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commenceInvalidToken(request, response, exception);
        }
    }

    private static boolean changesState(HttpServletRequest request) {
        return !READ_ONLY_METHODS.contains(request.getMethod());
    }
}
