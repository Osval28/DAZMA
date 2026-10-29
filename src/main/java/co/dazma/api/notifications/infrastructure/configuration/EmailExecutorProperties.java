package co.dazma.api.notifications.infrastructure.configuration;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "dazma.notifications.email-executor")
public record EmailExecutorProperties(
        @Positive int threads,
        @PositiveOrZero int queueCapacity,
        @NotNull Duration shutdownTimeout
) {
}