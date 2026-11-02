package co.dazma.api.publications.infrastructure.output.repositories.nonstructured.cache;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.UUID;

@Validated
@ConfigurationProperties(prefix = "dazma.cache.publications")
public record PublicationCacheProperties(
        @NotBlank String keyPrefix,
        @NotNull @DurationMin(seconds = 1) Duration ttl
) {

    public String keyFor(UUID publicationId) {
        return keyPrefix + publicationId;
    }
}
