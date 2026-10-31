package co.dazma.api.security.infrastructure.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@Validated
@ConfigurationProperties(prefix = "dazma.security.origin")
public record OriginSecretProperties(@NotEmpty Set<@NotBlank String> secrets) {
}
