package co.dazma.api.publications.infrastructure.output.repositories.nonstructured.cache;

import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.infrastructure.output.repositories.nonstructured.cache.entities.PublicationCacheEntity;
import co.dazma.api.publications.services.output.ports.PublicationCachePort;
import co.dazma.api.shared.domain.errors.DomainInvalidException;
import co.dazma.api.shared.infrastructure.DomainMapper;
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
    private final DomainMapper<PublicationCacheEntity, Publication> cacheMapper;
    private final PublicationCacheProperties properties;

    public RedisPublicationCacheAdapter(StringRedisTemplate redisTemplate,
                                        JsonMapper jsonMapper,
                                        DomainMapper<PublicationCacheEntity, Publication> cacheMapper,
                                        PublicationCacheProperties properties) {
        this.redisTemplate = redisTemplate;
        this.jsonMapper = jsonMapper;
        this.cacheMapper = cacheMapper;
        this.properties = properties;
    }

    @Override
    public Optional<Publication> findById(UUID publicationId) {
        String key = properties.keyFor(publicationId);
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) {
                return Optional.empty();
            }
            PublicationCacheEntity entity = jsonMapper.readValue(json, PublicationCacheEntity.class);
            return Optional.of(cacheMapper.toDomain(entity));
        } catch (DataAccessException | JacksonException exception) {
            log.warn("Could not read publication from cache; falling back to the database. key={}, cause={}",
                    key, describe(exception));
            return Optional.empty();
        } catch (DomainInvalidException exception) {
            log.warn("Cached publication no longer satisfies domain rules; discarding it. key={}, cause={}",
                    key, exception.getClass().getSimpleName());
            evict(publicationId);
            return Optional.empty();
        }
    }

    @Override
    public void save(Publication publication) {
        String key = properties.keyFor(publication.getId());
        try {
            String json = jsonMapper.writeValueAsString(cacheMapper.toEntity(publication));
            redisTemplate.opsForValue().set(key, json, properties.ttl());
        } catch (DataAccessException | JacksonException exception) {
            log.warn("Could not write publication to cache. key={}, cause={}",
                    key, describe(exception));
        }
    }

    @Override
    public void evict(UUID publicationId) {
        String key = properties.keyFor(publicationId);
        try {
            redisTemplate.delete(key);
        } catch (DataAccessException exception) {
            log.warn("Could not evict publication from cache; it will expire by TTL. key={}, cause={}",
                    key, describe(exception));
        }
    }

    /**
     * Includes the root cause (for example, a timeout or an authentication error) so connection
     * problems can be diagnosed. Lettuce messages do not contain the password.
     */
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
