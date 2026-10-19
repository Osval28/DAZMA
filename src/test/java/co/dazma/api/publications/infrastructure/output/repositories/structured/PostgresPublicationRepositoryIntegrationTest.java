package co.dazma.api.publications.infrastructure.output.repositories.structured;

import co.dazma.api.TestcontainersConfiguration;
import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationPhoto;
import co.dazma.api.publications.domain.PublicationType;
import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.entities.PublicationEntityJPA;
import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.repository.SpringDataPublicationRepository;
import co.dazma.api.publications.infrastructure.output.repositories.structured.mapper.PublicationMapStructDomainMapper;
import co.dazma.api.publications.infrastructure.output.repositories.structured.mapper.PublicationMapStructJpaMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class PostgresPublicationRepositoryIntegrationTest {

    @Autowired
    private SpringDataPublicationRepository springDataRepository;

    @Autowired
    private PublicationMapStructDomainMapper domainMapper;

    @Autowired
    private PublicationMapStructJpaMapper jpaMapper;

    @Test
    void shouldPersistTheMainPublicationData() {
        UUID ownerId = UUID.randomUUID();
        Publication publication = publication(ownerId, "publications/bicycle.jpg");

        springDataRepository.saveAndFlush(toJpaEntity(publication));
        PublicationEntityJPA found = springDataRepository.findById(publication.getId()).orElseThrow();

        assertEquals(publication.getId(), found.getId());
        assertEquals(ownerId, found.getOwnerId());
        assertEquals("Bicicleta", found.getName());
        assertEquals(PublicationType.EXCHANGE, found.getType());
        assertEquals("Bicicleta en buen estado", found.getDescription());
        assertEquals("Rionegro", found.getCity());
        assertEquals(publication.getStatus(), found.getStatus());
        assertEquals(publication.getCreatedAt(), found.getCreatedAt());
        assertEquals(publication.getPrice(), found.getPrice());
        assertEquals(1, found.getPhotos().size());
        assertEquals("publications/bicycle.jpg", found.getPhotos().get(0).getObjectKey());
    }


    private PublicationEntityJPA toJpaEntity(Publication publication) {
        return jpaMapper.toJpaEntity(domainMapper.toEntity(publication));
    }

    private Publication publication(UUID ownerId, String objectKey) {
        return Publication.publish(
                ownerId,
                "Bicicleta",
                PublicationType.EXCHANGE,
                "Bicicleta en buen estado",
                "Rionegro",
                List.of(new PublicationPhoto(UUID.randomUUID(), objectKey, 0)),
                Instant.parse("2026-10-18T12:00:00Z"),
                850_000
        );
    }
}
