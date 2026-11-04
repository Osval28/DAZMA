package co.dazma.api.chats.infrastructure.output.repositories.structured;

import co.dazma.api.chats.domain.models.ChatOffer;
import co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.entities.ChatOfferEntityJPA;
import co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.repositories.SpringDataChatOfferRepository;
import co.dazma.api.chats.infrastructure.output.repositories.structured.mapper.ChatOfferMapStructDomainMapper;
import co.dazma.api.chats.infrastructure.output.repositories.structured.mapper.ChatOfferMapStructJpaMapper;
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

class PostgresqlChatOfferAdapterTest {

    private SpringDataChatOfferRepository springDataRepository;
    private PostgresqlChatOfferAdapter adapter;

    @BeforeEach
    void setUp() {
        springDataRepository = mock(SpringDataChatOfferRepository.class);
        adapter = new PostgresqlChatOfferAdapter(
                springDataRepository,
                Mappers.getMapper(ChatOfferMapStructDomainMapper.class),
                Mappers.getMapper(ChatOfferMapStructJpaMapper.class)
        );
    }

    @Test
    void shouldMapTheOfferToAJpaEntityBeforeSaving() {
        UUID publicationId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        adapter.save(new ChatOffer(publicationId, ownerId));

        ArgumentCaptor<ChatOfferEntityJPA> captor = ArgumentCaptor.forClass(ChatOfferEntityJPA.class);
        verify(springDataRepository).save(captor.capture());
        assertEquals(publicationId, captor.getValue().getPublicationId());
        assertEquals(ownerId, captor.getValue().getOwnerId());
    }

    @Test
    void shouldFindTheOfferByPublication() {
        UUID publicationId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        when(springDataRepository.findById(publicationId))
                .thenReturn(Optional.of(new ChatOfferEntityJPA(publicationId, ownerId)));

        assertEquals(Optional.of(new ChatOffer(publicationId, ownerId)), adapter.findByPublicationId(publicationId));
    }

    @Test
    void shouldReturnEmptyWhenThePublicationIsNotInTheLocalCopy() {
        UUID publicationId = UUID.randomUUID();
        when(springDataRepository.findById(publicationId)).thenReturn(Optional.empty());

        assertTrue(adapter.findByPublicationId(publicationId).isEmpty());
    }
}
