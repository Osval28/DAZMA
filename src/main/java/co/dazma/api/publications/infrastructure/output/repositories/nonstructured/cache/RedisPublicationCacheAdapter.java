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
            log.warn("No se pudo leer la publicación de la caché; se usará la base de datos. key={}, causa={}",
                    key, exception.getClass().getSimpleName());
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
            String json = jsonMapper.writeValueAsString(cacheMapper.toEntity(publication));
            redisTemplate.opsForValue().set(key, json, properties.ttl());
        } catch (DataAccessException | JacksonException exception) {
            log.warn("No se pudo guardar la publicación en la caché. key={}, causa={}",
                    key, exception.getClass().getSimpleName());
        }
    }

    @Override
    public void evict(UUID publicationId) {
        String key = properties.keyFor(publicationId);
        try {
            redisTemplate.delete(key);
        } catch (DataAccessException exception) {
            log.warn("No se pudo invalidar la publicación en la caché; expirará por TTL. key={}, causa={}",
                    key, exception.getClass().getSimpleName());
        }
    }
}