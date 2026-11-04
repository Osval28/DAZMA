package co.dazma.api.chats.infrastructure.output.repositories.structured;

import co.dazma.api.TestcontainersConfiguration;
import co.dazma.api.chats.domain.models.ChatOffer;
import co.dazma.api.chats.services.output.ports.ChatOfferRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class PostgresChatOfferRepositoryIntegrationTest {

    @Autowired
    private ChatOfferRepositoryPort offerRepository;

    @Test
    void shouldPersistAndFindAnOfferByPublication() {
        ChatOffer offer = new ChatOffer(UUID.randomUUID(), UUID.randomUUID());

        offerRepository.save(offer);

        assertEquals(Optional.of(offer), offerRepository.findByPublicationId(offer.publicationId()));
    }

    @Test
    void shouldKeepASingleOfferWhenTheSamePublicationIsSavedTwice() {
        UUID publicationId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        offerRepository.save(new ChatOffer(publicationId, ownerId));
        offerRepository.save(new ChatOffer(publicationId, ownerId));

        assertEquals(Optional.of(new ChatOffer(publicationId, ownerId)), offerRepository.findByPublicationId(publicationId));
    }

    @Test
    void shouldReturnEmptyWhenTheOfferDoesNotExist() {
        assertTrue(offerRepository.findByPublicationId(UUID.randomUUID()).isEmpty());
    }
}
