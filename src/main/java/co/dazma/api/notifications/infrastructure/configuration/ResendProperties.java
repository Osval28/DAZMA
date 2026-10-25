package co.dazma.api.notifications.infrastructure.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "dazma.notifications.resend")

public record ResendProperties(@NotBlank String apiKey, @NotBlank String sender, @NotNull Duration connectTimeout, @NotNull Duration readTimeout) {




}
