package co.dazma.api.notifications.infrastructure.output.repositories.structured;

import co.dazma.api.notifications.domain.Recipient;
import co.dazma.api.notifications.infrastructure.output.repositories.structured.jpa.entities.RecipientEntityJPA;
import co.dazma.api.notifications.infrastructure.output.repositories.structured.jpa.repositories.SpringDataRecipientRepository;
import co.dazma.api.notifications.infrastructure.output.repositories.structured.mapper.RecipientMapStructDomainMapper;
import co.dazma.api.notifications.infrastructure.output.repositories.structured.mapper.RecipientMapStructJpaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostgresqlRecipientRepositoryAdapterTest {

    private SpringDataRecipientRepository springDataRepository;
    private PostgresqlRecipientRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        springDataRepository = mock(SpringDataRecipientRepository.class);
        adapter = new PostgresqlRecipientRepositoryAdapter(
                springDataRepository,
                Mappers.getMapper(RecipientMapStructDomainMapper.class),
                Mappers.getMapper(RecipientMapStructJpaMapper.class)
        );
    }

    @Test
    void shouldMapTheRecipientToAJpaEntityBeforeSaving() {
        UUID userId = UUID.randomUUID();

        adapter.save(new Recipient(userId, "ana@dazma.store"));

        ArgumentCaptor<RecipientEntityJPA> captor = ArgumentCaptor.forClass(RecipientEntityJPA.class);
        verify(springDataRepository).save(captor.capture());
        assertEquals(userId, captor.getValue().getUserId());
        assertEquals("ana@dazma.store", captor.getValue().getEmail());
    }

    @Test
    void shouldFindTheRecipientByUserId() {
        UUID userId = UUID.randomUUID();
        when(springDataRepository.findById(userId))
                .thenReturn(Optional.of(new RecipientEntityJPA(userId, "ana@dazma.store")));

        Optional<Recipient> recipient = adapter.findByUserId(userId);

        assertEquals(Optional.of(new Recipient(userId, "ana@dazma.store")), recipient);
    }

    @Test
    void shouldReturnEmptyWhenTheRecipientDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(springDataRepository.findById(userId)).thenReturn(Optional.empty());

        assertTrue(adapter.findByUserId(userId).isEmpty());
    }
}
