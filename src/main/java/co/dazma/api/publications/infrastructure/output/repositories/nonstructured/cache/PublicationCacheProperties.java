package co.dazma.api.publications.infrastructure.output.repositories.nonstructured.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@ConfigurationProperties(prefix = "dazma.cache.publications")
public record PublicationCacheProperties(String keyPrefix, Duration ttl) {

    public PublicationCacheProperties {
        Objects.requireNonNull(keyPrefix, "El prefijo de llaves de la caché de publicaciones es obligatorio");
        Objects.requireNonNull(ttl, "El TTL de la caché de publicaciones es obligatorio");

        if (keyPrefix.isBlank()) {
            throw new IllegalArgumentException("El prefijo de llaves de la caché de publicaciones no puede estar vacío");
        }
        if (ttl.isZero() || ttl.isNegative()) {
            throw new IllegalArgumentException("El TTL de la caché de publicaciones debe ser positivo");
        }
    }

    public String keyFor(UUID publicationId) {
        return keyPrefix + publicationId;
    }
}