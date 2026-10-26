package co.dazma.api.publications.infrastructure.output.repositories.nonstructured.cache;

import co.dazma.api.publications.domain.Description;
import co.dazma.api.publications.domain.Name;
import co.dazma.api.publications.domain.Price;
import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationPhoto;
import co.dazma.api.publications.domain.PublicationStatus;
import co.dazma.api.publications.domain.PublicationType;
import co.dazma.api.publications.infrastructure.output.repositories.nonstructured.cache.mapper.PublicationMapStructCacheMapper;
import co.dazma.api.shared.domain.Municipality;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RedisPublicationCacheAdapterTest {

    private static final Duration TTL = Duration.ofMinutes(10);
    private static final String KEY_PREFIX = "dazma:publications:v1:";

    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private RedisPublicationCacheAdapter adapter;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        adapter = new RedisPublicationCacheAdapter(
                redisTemplate,
                JsonMapper.builder().build(),
                Mappers.getMapper(PublicationMapStructCacheMapper.class),
                new PublicationCacheProperties(KEY_PREFIX, TTL)
        );
    }

    @Test
    void shouldStoreAndReadBackTheSamePublication() {
        Publication publication = publication();
        String key = KEY_PREFIX + publication.getId();

        String json = storedJson(publication);
        when(valueOperations.get(key)).thenReturn(json);
        Publication cached = adapter.findById(publication.getId()).orElseThrow();

        assertEquals(publication.getId(), cached.getId());
        assertEquals(publication.getOwnerId(), cached.getOwnerId());
        assertEquals(publication.getName(), cached.getName());
        assertEquals(publication.getType(), cached.getType());
        assertEquals(publication.getDescription(), cached.getDescription());
        assertEquals(publication.getMunicipality(), cached.getMunicipality());
        assertEquals(publication.getStatus(), cached.getStatus());
        assertEquals(publication.getCreatedAt(), cached.getCreatedAt());
        assertEquals(publication.getPrice(), cached.getPrice());
        assertEquals(publication.getPhotos().size(), cached.getPhotos().size());
        assertEquals(
                publication.getPhotos().get(0).getObjectKey(),
                cached.getPhotos().get(0).getObjectKey()
        );
    }

    @Test
    void shouldReturnEmptyWhenTheKeyDoesNotExist() {
        UUID publicationId = UUID.randomUUID();
        when(valueOperations.get(KEY_PREFIX + publicationId)).thenReturn(null);

        assertTrue(adapter.findById(publicationId).isEmpty());
    }

    @Test
    void shouldFallBackToEmptyWhenRedisIsUnavailable() {
        UUID publicationId = UUID.randomUUID();
        when(valueOperations.get(anyString()))
                .thenThrow(new RedisConnectionFailureException("Redis caído"));

        assertTrue(adapter.findById(publicationId).isEmpty());
    }

    @Test
    void shouldDiscardAnEntryThatNoLongerSatisfiesTheDomainRules() {
        Publication publication = publication();
        String key = KEY_PREFIX + publication.getId();
        String invalidJson = storedJson(publication).replace("\"Bicicleta\"", "\" \"");
        when(valueOperations.get(key)).thenReturn(invalidJson);

        Optional<Publication> cached = adapter.findById(publication.getId());

        assertTrue(cached.isEmpty());
        verify(redisTemplate).delete(key);
    }

    private String storedJson(Publication publication) {
        adapter.save(publication);
        ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(eq(KEY_PREFIX + publication.getId()), json.capture(), eq(TTL));
        return json.getValue();
    }

    private static Publication publication() {
        return Publication.restore(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new Name("Bicicleta"),
                PublicationType.EXCHANGE,
                new Description("Bicicleta de montaña en buen estado"),
                new Municipality("Rionegro"),
                PublicationStatus.PUBLISHED,
                List.of(new PublicationPhoto(UUID.randomUUID(), "publications/owner/photo-0", 0)),
                Instant.parse("2026-10-20T20:00:00Z"),
                new Price(900_000)
        );
    }
}
