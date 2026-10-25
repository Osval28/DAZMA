package co.dazma.api.publications.infrastructure.configuration;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Validated
@ConfigurationProperties(prefix = "dazma.storage.r2")
public record CloudflareR2Properties(
        @NotBlank @URL(protocol = "https") String endpoint,
        @NotBlank String accessKeyId,
        @NotBlank String secretAccessKey,
        @NotBlank String bucketName
) {

    public URI endpointUri() {
        return URI.create(endpoint);
    }

}
