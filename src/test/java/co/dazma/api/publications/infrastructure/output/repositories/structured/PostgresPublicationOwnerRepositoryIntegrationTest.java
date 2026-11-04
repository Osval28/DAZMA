package co.dazma.api.publications.infrastructure.output.repositories.structured;

import co.dazma.api.TestcontainersConfiguration;
import co.dazma.api.publications.domain.models.PublicationOwner;
import co.dazma.api.publications.domain.enums.PublicationOwnerState;
import co.dazma.api.publications.services.output.ports.PublicationOwnerRepositoryPort;
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
class PostgresPublicationOwnerRepositoryIntegrationTest {

    @Autowired
    private PublicationOwnerRepositoryPort ownerRepository;

    @Test
    void shouldPersistAndFindAnOwnerByExternalIdUsingPostgres() {
        UUID userId = UUID.randomUUID();
        String externalId = "firebase-" + userId;

        ownerRepository.save(new PublicationOwner(userId, "Ana", externalId, PublicationOwnerState.ACTIVE));

        assertEquals(
                Optional.of(new PublicationOwner(userId, "Ana", externalId, PublicationOwnerState.ACTIVE)),
                ownerRepository.findByExternalId(externalId)
        );
    }

    @Test
    void shouldKeepASingleCopyWhenTheSameEventIsProcessedTwice() {
        UUID userId = UUID.randomUUID();
        String externalId = "firebase-" + userId;
        PublicationOwner owner = new PublicationOwner(userId, "Ana", externalId, PublicationOwnerState.ACTIVE);

        ownerRepository.save(owner);
        ownerRepository.save(owner);

        assertEquals(Optional.of(owner), ownerRepository.findByExternalId(externalId));
    }

    @Test
    void shouldTreatAnInjectionPayloadAsAnExactExternalId() {
        UUID userId = UUID.randomUUID();
        String externalId = "firebase-" + userId;
        PublicationOwner owner = new PublicationOwner(userId, "Ana", externalId, PublicationOwnerState.ACTIVE);
        ownerRepository.save(owner);

        assertEquals(Optional.empty(), ownerRepository.findByExternalId("' OR '1'='1"));
        assertEquals(Optional.of(owner), ownerRepository.findByExternalId(externalId));
    }
}
