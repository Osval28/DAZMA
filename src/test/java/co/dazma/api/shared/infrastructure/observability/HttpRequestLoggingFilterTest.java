package co.dazma.api.shared.infrastructure.observability;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.event.KeyValuePair;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpRequestLoggingFilterTest {

    private static final String SESSION_COOKIE = "dazma_session=secret-session";
    private static final String AUTHORIZATION = "Bearer secret-token";

    private final HttpRequestLoggingFilter filter = new HttpRequestLoggingFilter();
    private final Logger filterLogger =
            (Logger) LoggerFactory.getLogger(HttpRequestLoggingFilter.class);
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void attachAppender() {
        appender = new ListAppender<>();
        appender.start();
        filterLogger.addAppender(appender);
    }

    @AfterEach
    void detachAppender() {
        filterLogger.detachAppender(appender);
        appender.stop();
    }

    @Test
    void shouldLogCompletedRequestWithNormalizedRouteAndWithoutSensitiveData() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/publications/api/v1/publications/123");
        request.setQueryString("token=secret-query-value");
        request.addHeader("Authorization", AUTHORIZATION);
        request.addHeader("Cookie", SESSION_COOKIE);
        request.setAttribute(
                HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE,
                "/publications/api/v1/publications/{publicationId}"
        );
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        ILoggingEvent event = onlyEvent();
        Map<String, String> fields = fields(event);
        assertEquals(Level.INFO, event.getLevel());
        assertEquals("http.request.completed", fields.get("event.name"));
        assertEquals("GET", fields.get("http.request.method"));
        assertEquals("/publications/api/v1/publications/{publicationId}", fields.get("http.route"));
        assertEquals("200", fields.get("http.response.status_code"));
        assertTrue(Long.parseLong(fields.get("duration_ms")) >= 0);

        String serializedEvent = event.toString();
        assertFalse(serializedEvent.contains("secret-query-value"));
        assertFalse(serializedEvent.contains(AUTHORIZATION));
        assertFalse(serializedEvent.contains(SESSION_COOKIE));
        assertFalse(serializedEvent.contains("/publications/api/v1/publications/123"));
    }

    @Test
    void shouldLogServerFailureAtWarnLevelAndRethrowIt() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/publications/api/v1/publications");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain failingChain = (servletRequest, servletResponse) -> {
            throw new ServletException("database unavailable");
        };

        assertThrows(ServletException.class, () -> filter.doFilter(request, response, failingChain));

        ILoggingEvent event = onlyEvent();
        Map<String, String> fields = fields(event);
        assertEquals(Level.WARN, event.getLevel());
        assertEquals("UNKNOWN", fields.get("http.route"));
        assertEquals("500", fields.get("http.response.status_code"));
    }

    private ILoggingEvent onlyEvent() {
        assertEquals(1, appender.list.size());
        return appender.list.getFirst();
    }

    private static Map<String, String> fields(ILoggingEvent event) {
        List<KeyValuePair> keyValuePairs = event.getKeyValuePairs();
        return keyValuePairs.stream()
                .collect(Collectors.toMap(pair -> pair.key, pair -> String.valueOf(pair.value)));
    }
}
