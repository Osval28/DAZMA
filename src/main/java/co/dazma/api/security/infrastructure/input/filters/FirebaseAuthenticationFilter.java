package co.dazma.api.security.infrastructure.input.filters;

import co.dazma.api.security.domain.InvalidIdentityTokenException;
import co.dazma.api.security.infrastructure.input.errors.ApiAuthenticationEntryPoint;
import co.dazma.api.security.services.input.ports.AuthenticateIdentityUseCase;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthenticateIdentityUseCase authenticateIdentityUseCase;
    private final ApiAuthenticationEntryPoint authenticationEntryPoint;

    public FirebaseAuthenticationFilter(
            AuthenticateIdentityUseCase authenticateIdentityUseCase,
            ApiAuthenticationEntryPoint authenticationEntryPoint
    ) {
        this.authenticateIdentityUseCase = Objects.requireNonNull(
                authenticateIdentityUseCase,
                "El caso de uso de autenticación es obligatorio"
        );
        this.authenticationEntryPoint = Objects.requireNonNull(
                authenticationEntryPoint,
                "El punto de entrada de autenticación es obligatorio"
        );
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();

        try {
            String uid = authenticateIdentityUseCase.authenticate(token);

            UsernamePasswordAuthenticationToken authentication =
                    UsernamePasswordAuthenticationToken.authenticated(uid, null, List.of());
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
}
