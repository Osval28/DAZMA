package co.dazma.api.chats.infrastructure.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "dazma.chats.ably")
public record AblyProperties(
        @NotBlank @Pattern(regexp = "^[^:\\s]+:[^:\\s]+$") String apiKey,
        @NotNull @DurationMin(minutes = 1) Duration tokenValidity,
        @NotBlank @Pattern(regexp = "^[a-z0-9-]+$") String roomNamespace
) {

    private static final String KEY_SEPARATOR = ":";

    public String keyName() {
        return apiKey.substring(0, apiKey.indexOf(KEY_SEPARATOR));
    }

    public String keySecret() {
        return apiKey.substring(apiKey.indexOf(KEY_SEPARATOR) + 1);
    }

    @Override
    public String toString() {
        return "AblyProperties[keyName=" + keyName()
                + ", tokenValidity=" + tokenValidity
                + ", roomNamespace=" + roomNamespace + "]";
    }
}