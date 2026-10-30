package co.dazma.api.shared.infrastructure.events;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "dazma.events.resubmission")
public record EventResubmissionProperties(
        @NotNull Duration minAge,
        @Positive int maxAttempts,
        @Positive int batchSize
) {
}