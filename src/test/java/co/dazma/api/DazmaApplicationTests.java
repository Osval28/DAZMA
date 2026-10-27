package co.dazma.api;

import co.dazma.api.notifications.domain.EmailMessage;
import co.dazma.api.notifications.infrastructure.output.externalservices.resend.NotificationDeliveryException;
import co.dazma.api.notifications.services.output.ports.NotificationGateway;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.core.net.RequestOptions;
import com.resend.services.emails.Emails;
import com.resend.services.emails.model.CreateEmailOptions;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class DazmaApplicationTests {

    @Autowired
    private NotificationGateway notificationGateway;

    @Autowired
    private RetryRegistry retryRegistry;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private MeterRegistry meterRegistry;

    @MockitoBean
    private Resend resend;

    @Test
    void contextLoads() {
        assertTrue(AopUtils.isAopProxy(notificationGateway));
        var retry = retryRegistry.retry("resend");
        var circuitBreaker = circuitBreakerRegistry.circuitBreaker("resend");
        assertNotNull(retry);
        assertNotNull(circuitBreaker);
        assertEquals(3, retry.getRetryConfig().getMaxAttempts());
        assertEquals(10, circuitBreaker.getCircuitBreakerConfig().getSlidingWindowSize());
        assertEquals(5, circuitBreaker.getCircuitBreakerConfig().getMinimumNumberOfCalls());
    }

    @Test
    void shouldRetryTransientResendFailuresAndThenSucceed() throws ResendException {
        Emails emails = mock(Emails.class);
        when(resend.emails()).thenReturn(emails);
        when(emails.send(any(CreateEmailOptions.class), any(RequestOptions.class)))
                .thenThrow(new ResendException("unavailable", 503, "{}"))
                .thenThrow(new ResendException("unavailable", 503, "{}"))
                .thenReturn(null);

        notificationGateway.send(emailMessage("transient-failure"));

        verify(emails, times(3)).send(
                any(CreateEmailOptions.class),
                any(RequestOptions.class)
        );
    }

    @Test
    void shouldNotRetryPermanentResendFailures() throws ResendException {
        Emails emails = mock(Emails.class);
        when(resend.emails()).thenReturn(emails);
        when(emails.send(any(CreateEmailOptions.class), any(RequestOptions.class)))
                .thenThrow(new ResendException("invalid request", 400, "{}"));

        assertThrows(
                NotificationDeliveryException.class,
                () -> notificationGateway.send(emailMessage("permanent-failure"))
        );

        verify(emails).send(any(CreateEmailOptions.class), any(RequestOptions.class));
    }

    @Test
    void shouldOpenBlockAndRecoverTheResendCircuitBreaker() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("resend");
        circuitBreaker.reset();
        NotificationDeliveryException transientFailure =
                new NotificationDeliveryException(503, true, null);

        try {
            for (int call = 0; call < 5; call++) {
                assertThrows(
                        NotificationDeliveryException.class,
                        () -> circuitBreaker.executeRunnable(() -> {
                            throw transientFailure;
                        })
                );
            }

            assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
            assertThrows(
                    CallNotPermittedException.class,
                    () -> circuitBreaker.executeRunnable(() -> {
                        throw new AssertionError("No debe invocarse mientras el circuito está abierto");
                    })
            );

            circuitBreaker.transitionToHalfOpenState();
            circuitBreaker.executeRunnable(() -> { });
            circuitBreaker.executeRunnable(() -> { });

            assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
        } finally {
            circuitBreaker.reset();
        }
    }

    @Test
    void shouldPublishResilienceMetricsThroughMicrometer() {
        retryRegistry.retry("resend");
        circuitBreakerRegistry.circuitBreaker("resend");

        assertNotNull(meterRegistry.find("resilience4j.retry.calls")
                .tag("name", "resend")
                .functionCounter());
        assertNotNull(meterRegistry.find("resilience4j.circuitbreaker.calls")
                .tag("name", "resend")
                .timer());
        assertNotNull(meterRegistry.find("resilience4j.circuitbreaker.state")
                .tag("name", "resend")
                .tag("state", "closed")
                .gauge());
    }

    private static EmailMessage emailMessage(String idempotencyKey) {
        return new EmailMessage(
                "recipient@dazma.store",
                "DAZMA notification",
                "<p>Notification body</p>",
                idempotencyKey
        );
    }
}
