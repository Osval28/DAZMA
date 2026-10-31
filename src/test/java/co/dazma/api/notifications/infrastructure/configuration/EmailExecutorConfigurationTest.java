package co.dazma.api.notifications.infrastructure.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailExecutorConfigurationTest {

    @Test
    void shouldUseAFixedNumberOfThreadsForEmails() {
        ThreadPoolTaskExecutor executor = new EmailExecutorConfiguration().emailTaskExecutor(
                new EmailExecutorProperties(4, 100, Duration.ofSeconds(30))
        );

        assertEquals(4, executor.getCorePoolSize());
        assertEquals(4, executor.getMaxPoolSize());
        assertEquals("dazma-email-", executor.getThreadNamePrefix());
    }
}
