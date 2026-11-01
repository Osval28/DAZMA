package co.dazma.api.publications.infrastructure.output.repositories.structured;

import co.dazma.api.publications.domain.PublicationOwner;
import co.dazma.api.publications.domain.PublicationOwnerState;
import co.dazma.api.publications.infrastructure.output.repositories.structured.entities.PublicationOwnerStateEntity;
import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.entities.PublicationOwnerEntityJPA;
import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.repository.SpringDataPublicationOwnerRepository;
import co.dazma.api.publications.infrastructure.output.repositories.structured.mapper.PublicationOwnerMapStructDomainMapper;
import co.dazma.api.publications.infrastructure.output.repositories.structured.mapper.PublicationOwnerMapStructJpaMapper;
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

class PostgresqlPublicationOwnerAdapterTest {

    private SpringDataPublicationOwnerRepository springDataRepository;
    private PostgresqlPublicationOwnerAdapter adapter;

    @BeforeEach
    void setUp() {
        springDataRepository = mock(SpringDataPublicationOwnerRepository.class);
        adapter = new PostgresqlPublicationOwnerAdapter(
                springDataRepository,
                Mappers.getMapper(PublicationOwnerMapStructDomainMapper.class),
                Mappers.getMapper(PublicationOwnerMapStructJpaMapper.class)
        );
    }

    @Test
    void shouldMapTheOwnerToAJpaEntityBeforeSaving() {
        UUID userId = UUID.randomUUID();

        adapter.save(new PublicationOwner(userId, "Ana", "firebase-123", PublicationOwnerState.ACTIVE));

        ArgumentCaptor<PublicationOwnerEntityJPA> captor = ArgumentCaptor.forClass(PublicationOwnerEntityJPA.class);
        verify(springDataRepository).save(captor.capture());
        assertEquals(userId, captor.getValue().getUserId());
        assertEquals("Ana", captor.getValue().getUserName());
        assertEquals("firebase-123", captor.getValue().getExternalId());
        assertEquals(PublicationOwnerStateEntity.ACTIVE, captor.getValue().getState());
    }

    @Test
    void shouldFindTheOwnerByExternalId() {
        UUID userId = UUID.randomUUID();
        when(springDataRepository.findByExternalId("firebase-123")).thenReturn(Optional.of(
                new PublicationOwnerEntityJPA(userId, "Ana", "firebase-123", PublicationOwnerStateEntity.ACTIVE)
        ));

        assertEquals(
                Optional.of(new PublicationOwner(userId, "Ana", "firebase-123", PublicationOwnerState.ACTIVE)),
                adapter.findByExternalId("firebase-123")
        );
    }

    @Test
    void shouldReturnEmptyWhenTheOwnerIsNotInTheLocalCopy() {
        when(springDataRepository.findByExternalId("firebase-123")).thenReturn(Optional.empty());

        assertTrue(adapter.findByExternalId("firebase-123").isEmpty());
    }
}
