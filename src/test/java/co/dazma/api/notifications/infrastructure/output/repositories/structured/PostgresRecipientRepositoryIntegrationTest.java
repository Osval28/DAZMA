package co.dazma.api.notifications.infrastructure.output.repositories.structured;

import co.dazma.api.TestcontainersConfiguration;
import co.dazma.api.notifications.domain.Recipient;
import co.dazma.api.notifications.services.output.ports.RecipientRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class PostgresRecipientRepositoryIntegrationTest {

    @Autowired
    private RecipientRepositoryPort recipientRepository;

    @Test
    void shouldPersistAndFindARecipientUsingPostgres() {
        UUID userId = UUID.randomUUID();

        recipientRepository.save(new Recipient(userId, "ana@dazma.store"));

        assertEquals(
                Optional.of(new Recipient(userId, "ana@dazma.store")),
                recipientRepository.findByUserId(userId)
        );
    }

    @Test
    void shouldOverwriteTheEmailWhenTheSameUserIsSavedAgain() {
        UUID userId = UUID.randomUUID();

        recipientRepository.save(new Recipient(userId, "ana@dazma.store"));
        recipientRepository.save(new Recipient(userId, "ana.nueva@dazma.store"));

        assertEquals(
                Optional.of(new Recipient(userId, "ana.nueva@dazma.store")),
                recipientRepository.findByUserId(userId)
        );
    }
}
