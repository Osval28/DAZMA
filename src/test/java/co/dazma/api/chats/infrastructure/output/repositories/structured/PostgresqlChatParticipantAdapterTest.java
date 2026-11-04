package co.dazma.api.chats.infrastructure.output.repositories.structured;

import co.dazma.api.chats.domain.models.ChatParticipant;
import co.dazma.api.chats.domain.enums.ChatParticipantState;
import co.dazma.api.chats.infrastructure.output.repositories.entities.ChatParticipantStateEntity;
import co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.entities.ChatParticipantEntityJPA;
import co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.repositories.SpringDataChatParticipantRepository;
import co.dazma.api.chats.infrastructure.output.repositories.structured.mapper.ChatParticipantMapStructDomainMapper;
import co.dazma.api.chats.infrastructure.output.repositories.structured.mapper.ChatParticipantMapStructJpaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostgresqlChatParticipantAdapterTest {

    private SpringDataChatParticipantRepository springDataRepository;
    private PostgresqlChatParticipantAdapter adapter;

    @BeforeEach
    void setUp() {
        springDataRepository = mock(SpringDataChatParticipantRepository.class);
        adapter = new PostgresqlChatParticipantAdapter(
                springDataRepository,
                Mappers.getMapper(ChatParticipantMapStructDomainMapper.class),
                Mappers.getMapper(ChatParticipantMapStructJpaMapper.class)
        );
    }

    @Test
    void shouldMapTheParticipantToAJpaEntityBeforeSaving() {
        UUID userId = UUID.randomUUID();

        adapter.save(new ChatParticipant(userId, "firebase-123", "Ana", ChatParticipantState.ACTIVE));

        ArgumentCaptor<ChatParticipantEntityJPA> captor = ArgumentCaptor.forClass(ChatParticipantEntityJPA.class);
        verify(springDataRepository).save(captor.capture());
        assertEquals(userId, captor.getValue().getUserId());
        assertEquals("firebase-123", captor.getValue().getExternalId());
        assertEquals(ChatParticipantStateEntity.ACTIVE, captor.getValue().getState());
    }

    @Test
    void shouldFindTheParticipantByExternalId() {
        UUID userId = UUID.randomUUID();
        when(springDataRepository.findByExternalId("firebase-123")).thenReturn(Optional.of(
                new ChatParticipantEntityJPA(userId, "firebase-123", "Ana", ChatParticipantStateEntity.SUSPENDED)
        ));

        assertEquals(
                Optional.of(new ChatParticipant(userId, "firebase-123", "Ana", ChatParticipantState.SUSPENDED)),
                adapter.findByExternalId("firebase-123")
        );
    }

    @Test
    void shouldFindSeveralParticipantsByTheirUserIds() {
        UUID userId = UUID.randomUUID();
        when(springDataRepository.findAllById(List.of(userId))).thenReturn(List.of(
                new ChatParticipantEntityJPA(userId, "firebase-123", "Ana", ChatParticipantStateEntity.ACTIVE)
        ));

        assertEquals(
                List.of(new ChatParticipant(userId, "firebase-123", "Ana", ChatParticipantState.ACTIVE)),
                adapter.findAllByUserIds(List.of(userId))
        );
    }

    @Test
    void shouldReturnEmptyWhenTheParticipantIsNotInTheLocalCopy() {
        when(springDataRepository.findByExternalId("firebase-123")).thenReturn(Optional.empty());

        assertTrue(adapter.findByExternalId("firebase-123").isEmpty());
    }
}