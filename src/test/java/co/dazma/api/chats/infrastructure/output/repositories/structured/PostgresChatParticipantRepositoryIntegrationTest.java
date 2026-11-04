package co.dazma.api.chats.infrastructure.output.repositories.structured;

import co.dazma.api.TestcontainersConfiguration;
import co.dazma.api.chats.domain.models.ChatParticipant;
import co.dazma.api.chats.domain.enums.ChatParticipantState;
import co.dazma.api.chats.services.output.ports.ChatParticipantRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        ChatParticipant participant = new ChatParticipant(userId, externalId, "Ana", ChatParticipantState.ACTIVE);

        participantRepository.save(participant);

        assertEquals(Optional.of(participant), participantRepository.findByExternalId(externalId));
    }

    @Test
    void shouldKeepASingleCopyWhenTheSameEventIsProcessedTwice() {
        UUID userId = UUID.randomUUID();
        String externalId = "firebase-" + userId;
        ChatParticipant participant = new ChatParticipant(userId, externalId, "Ana", ChatParticipantState.ACTIVE);

        participantRepository.save(participant);
        participantRepository.save(participant);

        assertEquals(Optional.of(participant), participantRepository.findByExternalId(externalId));
    }

    @Test
    void shouldFindSeveralParticipantsByTheirUserIdsUsingPostgres() {
        ChatParticipant first = new ChatParticipant(UUID.randomUUID(), "firebase-" + UUID.randomUUID(), "Ana", ChatParticipantState.ACTIVE);
        ChatParticipant second = new ChatParticipant(UUID.randomUUID(), "firebase-" + UUID.randomUUID(), "Luis", ChatParticipantState.ACTIVE);
        participantRepository.save(first);
        participantRepository.save(second);

        List<ChatParticipant> found = participantRepository.findAllByUserIds(List.of(first.userId(), second.userId()));

        assertEquals(2, found.size());
        assertTrue(found.containsAll(List.of(first, second)));
    }

    @Test
    void shouldReturnEmptyForAnUnknownExternalId() {
        assertTrue(participantRepository.findByExternalId("firebase-unknown").isEmpty());
    }
}