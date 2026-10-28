package co.dazma.api.shared.infrastructure.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class HttpRequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestLoggingFilter.class);
    private static final String UNKNOWN_ROUTE = "UNKNOWN";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startedAt = System.nanoTime();
        boolean completed = false;

        try {
            filterChain.doFilter(request, response);
            completed = true;
        } finally {
            int status = effectiveStatus(response, !completed);
            long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
            String route = resolvedRoute(request);

            LoggingEventBuilder event = status >= HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                    ? log.atWarn()
                    : log.atInfo();
            event.addKeyValue("event.name", "http.request.completed")
                    .addKeyValue("http.request.method", request.getMethod())
                    .addKeyValue("http.route", route)
                    .addKeyValue("http.response.status_code", status)
                    .addKeyValue("duration_ms", durationMillis)
                    .log(status >= HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                            ? "Petición HTTP completada con error"
                            : "Petición HTTP completada");
        }
    }

    private static int effectiveStatus(HttpServletResponse response, boolean failed) {
        if (failed && response.getStatus() < HttpServletResponse.SC_BAD_REQUEST) {
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
        return response.getStatus();
    }

    private static String resolvedRoute(HttpServletRequest request) {
        Object route = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return route == null ? UNKNOWN_ROUTE : route.toString();
    }
}
