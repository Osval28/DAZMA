package co.dazma.api.publications.infrastructure.output.repositories.structured;

import co.dazma.api.TestcontainersConfiguration;
import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationPhoto;
import co.dazma.api.publications.domain.PublicationType;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class PostgresPublicationRepositoryIntegrationTest {

    @Autowired
    private SpringDataPublicationRepository springDataRepository;

    @Autowired
    private UserRepositoryPort userRepository;

    @Test
    void shouldPersistTheMainPublicationData() {
        User owner = userRepository.save(User.register(
                "Ana",
                "3001234567",
                "Rionegro",
                "firebase-publication-owner"
        ));
        Publication publication = publication(owner.getId(), "publications/bicycle.jpg");

        springDataRepository.saveAndFlush(PublicationMapper.toEntity(publication));
        PublicationEntity found = springDataRepository.findById(publication.getId()).orElseThrow();

        assertEquals(publication.getId(), found.getId());
        assertEquals(owner.getId(), found.getOwnerId());
        assertEquals("Bicicleta", found.getName());
        assertEquals(PublicationType.EXCHANGE, found.getType());
        assertEquals("Bicicleta en buen estado", found.getDescription());
        assertEquals("Rionegro", found.getCity());
        assertEquals(publication.getStatus(), found.getStatus());
        assertEquals(publication.getCreatedAt(), found.getCreatedAt());
    }

    @Test
    void shouldRejectAPublicationWithAnUnknownOwner() {
        Publication publication = publication(
                UUID.randomUUID(),
                "publications/unknown-owner.jpg"
        );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> springDataRepository.saveAndFlush(
                        PublicationMapper.toEntity(publication)
                )
        );
    }

    private Publication publication(UUID ownerId, String objectKey) {
        return Publication.publish(
                ownerId,
                "Bicicleta",
                PublicationType.EXCHANGE,
                "Bicicleta en buen estado",
                "Rionegro",
                List.of(new PublicationPhoto(UUID.randomUUID(), objectKey, 0))
        );
    }
}
