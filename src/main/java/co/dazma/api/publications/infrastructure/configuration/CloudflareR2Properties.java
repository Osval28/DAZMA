package co.dazma.api.publications.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.util.Objects;

@ConfigurationProperties(prefix = "dazma.storage.r2")
public final class CloudflareR2Properties {

    private final URI endpoint;
    private final String accessKeyId;
    private final String secretAccessKey;
    private final String bucketName;

    public CloudflareR2Properties(
            URI endpoint,
            String accessKeyId,
            String secretAccessKey,
            String bucketName
    ) {
        this.endpoint = requireAbsoluteEndpoint(endpoint);
        this.accessKeyId = requireText(accessKeyId, "El identificador de acceso de R2 es obligatorio");
        this.secretAccessKey = requireText(
                secretAccessKey,
                "La clave secreta de acceso de R2 es obligatoria"
        );
        this.bucketName = requireText(bucketName, "El nombre del bucket de R2 es obligatorio");
    }

    public URI endpoint() {
        return endpoint;
    }
    
    public String accessKeyId() {
        return accessKeyId;
    }

    public String secretAccessKey() {
        return secretAccessKey;
    }

    public String bucketName() {
        return bucketName;
    }

    private static URI requireAbsoluteEndpoint(URI endpoint) {
        Objects.requireNonNull(endpoint, "El endpoint de R2 es obligatorio");
        if (!endpoint.isAbsolute() || endpoint.getHost() == null) {
            throw new IllegalArgumentException("El endpoint de R2 debe ser una dirección absoluta");
        }
        return endpoint;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
