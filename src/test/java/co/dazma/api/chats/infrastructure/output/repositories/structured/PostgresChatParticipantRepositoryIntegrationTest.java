package co.dazma.api.chats.infrastructure.output.repositories.structured;

import co.dazma.api.TestcontainersConfiguration;
import co.dazma.api.chats.domain.ChatParticipant;
import co.dazma.api.chats.domain.ChatParticipantState;
import co.dazma.api.chats.services.output.ports.ChatParticipantRepositoryPort;
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
class PostgresChatParticipantRepositoryIntegrationTest {

    @Autowired
    private ChatParticipantRepositoryPort participantRepository;

    @Test
    void shouldPersistAndFindAParticipantByExternalIdUsingPostgres() {
        UUID userId = UUID.randomUUID();
        String externalId = "firebase-" + userId;
        ChatParticipant participant = new ChatParticipant(userId, externalId, ChatParticipantState.ACTIVE);

        participantRepository.save(participant);

        assertEquals(Optional.of(participant), participantRepository.findByExternalId(externalId));
    }

    @Test
    void shouldKeepASingleCopyWhenTheSameEventIsProcessedTwice() {
        UUID userId = UUID.randomUUID();
        String externalId = "firebase-" + userId;
        ChatParticipant participant = new ChatParticipant(userId, externalId, ChatParticipantState.ACTIVE);

        participantRepository.save(participant);
        participantRepository.save(participant);

        assertEquals(Optional.of(participant), participantRepository.findByExternalId(externalId));
    }

    @Test
    void shouldReturnEmptyForAnUnknownExternalId() {
        assertTrue(participantRepository.findByExternalId("firebase-unknown").isEmpty());
    }
}