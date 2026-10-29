package co.dazma.api.security.infrastructure.input.filters;

import co.dazma.api.security.domain.SecurityErrorCode;
import co.dazma.api.security.infrastructure.input.errors.SecurityProblemWriter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Set;

public class OriginSecretFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Origin-Secret";
    private static final String HEALTH_PATH = "/actuator/health";

    private final List<byte[]> acceptedSecrets;
    private final SecurityProblemWriter problemWriter;

    public OriginSecretFilter(Set<String> acceptedSecrets, SecurityProblemWriter problemWriter) {
        this.acceptedSecrets = acceptedSecrets.stream()
                .map(secret -> secret.getBytes(StandardCharsets.UTF_8))
                .toList();
        this.problemWriter = problemWriter;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith(HEALTH_PATH);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (carriesAcceptedSecret(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        problemWriter.write(
                request,
                response,
                SecurityErrorCode.ORIGIN_NOT_ALLOWED,
                HttpStatus.FORBIDDEN,
                "error.title.forbidden"
        );
    }

    private boolean carriesAcceptedSecret(HttpServletRequest request) {
        String received = request.getHeader(HEADER);
        if (received == null) {
            return false;
        }
        byte[] receivedBytes = received.getBytes(StandardCharsets.UTF_8);
        return acceptedSecrets.stream().anyMatch(secret -> MessageDigest.isEqual(secret, receivedBytes));
    }
}
