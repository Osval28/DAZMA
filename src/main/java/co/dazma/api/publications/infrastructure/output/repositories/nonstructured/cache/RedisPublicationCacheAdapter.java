package co.dazma.api.publications.infrastructure.output.repositories.nonstructured.cache;

import co.dazma.api.publications.domain.models.Publication;
import co.dazma.api.publications.infrastructure.output.repositories.entities.PublicationEntity;
import co.dazma.api.publications.services.output.ports.PublicationCachePort;
import co.dazma.api.shared.domain.errors.DomainInvalidException;
import co.dazma.api.shared.infrastructure.DomainMapper;
import co.dazma.api.shared.infrastructure.observability.ExternalDependencyObservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RedisPublicationCacheAdapter implements PublicationCachePort {

    private static final Logger log = LoggerFactory.getLogger(RedisPublicationCacheAdapter.class);

    private final StringRedisTemplate redisTemplate;
    private final JsonMapper jsonMapper;
    private final DomainMapper<PublicationEntity, Publication> domainMapper;
    private final PublicationCacheProperties properties;
    private final ExternalDependencyObservation observations;

    public RedisPublicationCacheAdapter(StringRedisTemplate redisTemplate,
                                        JsonMapper jsonMapper,
                                        DomainMapper<PublicationEntity, Publication> domainMapper,
                                        PublicationCacheProperties properties,
                                        ExternalDependencyObservation observations) {
        this.redisTemplate = redisTemplate;
        this.jsonMapper = jsonMapper;
        this.domainMapper = domainMapper;
        this.properties = properties;
        this.observations = observations;
    }

    @Override
    public Optional<Publication> findById(UUID publicationId) {
        String key = properties.keyFor(publicationId);
        try {
            String json = observations.observe(
                    "redis",
                    "get_publication",
                    () -> redisTemplate.opsForValue().get(key)
            );
            if (json == null) {
                return Optional.empty();
            }
            PublicationEntity entity = jsonMapper.readValue(json, PublicationEntity.class);
            return Optional.of(domainMapper.toDomain(entity));
        } catch (DataAccessException | JacksonException exception) {
            log.warn("No se pudo leer la publicación de la caché; se usará la base de datos. key={}, causa={}",
                    key, describe(exception));
            return Optional.empty();
        } catch (DomainInvalidException exception) {
            log.warn("La publicación en caché ya no cumple las reglas del dominio; se descarta. key={}, causa={}",
                    key, exception.getClass().getSimpleName());
            evict(publicationId);
            return Optional.empty();
        }
    }

    @Override
    public void save(Publication publication) {
        String key = properties.keyFor(publication.getId());
        try {
            String json = jsonMapper.writeValueAsString(domainMapper.toEntity(publication));
            observations.observe(
                    "redis",
                    "save_publication",
                    () -> redisTemplate.opsForValue().set(key, json, properties.ttl())
            );
        } catch (DataAccessException | JacksonException exception) {
            log.warn("No se pudo guardar la publicación en la caché. key={}, causa={}",
                    key, describe(exception));
        }
    }

    @Override
    public void evict(UUID publicationId) {
        String key = properties.keyFor(publicationId);
        try {
            observations.observe(
                    "redis",
                    "evict_publication",
                    () -> redisTemplate.delete(key)
            );
        } catch (DataAccessException exception) {
            log.warn("No se pudo invalidar la publicación en la caché; expirará por TTL. key={}, causa={}",
                    key, describe(exception));
        }
    }


    private static String describe(Exception exception) {
        Throwable rootCause = exception;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        if (rootCause == exception) {
            return exception.getClass().getSimpleName();
        }
        return exception.getClass().getSimpleName()
                + " <- " + rootCause.getClass().getSimpleName()
                + ": " + rootCause.getMessage();
    }
}
