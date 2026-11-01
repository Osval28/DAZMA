package co.dazma.api.publications.infrastructure.output.repositories.structured;

import co.dazma.api.TestcontainersConfiguration;
import co.dazma.api.publications.domain.Description;
import co.dazma.api.publications.domain.Price;
import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.Name;
import co.dazma.api.publications.domain.PublicationPhoto;
import co.dazma.api.publications.domain.PublicationType;
import co.dazma.api.publications.infrastructure.output.repositories.structured.entities.PublicationStatusEntity;
import co.dazma.api.publications.infrastructure.output.repositories.structured.entities.PublicationTypeEntity;
import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.entities.PublicationEntityJPA;
import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.repository.SpringDataPublicationRepository;
import co.dazma.api.publications.infrastructure.output.repositories.structured.mapper.PublicationMapStructDomainMapper;
import co.dazma.api.publications.infrastructure.output.repositories.structured.mapper.PublicationMapStructJpaMapper;
import co.dazma.api.shared.domain.Municipality;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertEquals(PublicationTypeEntity.EXCHANGE, found.getType());
        assertEquals("Bicicleta en buen estado", found.getDescription());
        assertEquals("05615", found.getMunicipality());
        assertEquals(PublicationStatusEntity.PUBLISHED, found.getStatus());
        assertEquals(publication.getCreatedAt(), found.getCreatedAt());
        assertEquals(publication.getPrice().value(), found.getPrice());
        assertEquals(1, found.getPhotos().size());
        assertEquals("publications/bicycle.jpg", found.getPhotos().get(0).getObjectKey());
    }


    @Test
    void shouldListPublishedPublicationsOfAMunicipalityNewestFirstInSlices() {
        UUID ownerId = UUID.randomUUID();
        Publication oldest = publicationIn("05615", ownerId, "publications/a.jpg", "2026-10-18T12:00:00Z");
        Publication newest = publicationIn("05615", ownerId, "publications/b.jpg", "2026-10-20T12:00:00Z");
        Publication otherMunicipality = publicationIn("05440", ownerId, "publications/c.jpg", "2026-10-19T12:00:00Z");
        springDataRepository.saveAllAndFlush(List.of(
                toJpaEntity(oldest), toJpaEntity(newest), toJpaEntity(otherMunicipality)
        ));
        Sort newestFirst = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));

        Slice<PublicationEntityJPA> firstSlice = springDataRepository.findByStatusAndMunicipality(
                PublicationStatusEntity.PUBLISHED, "05615", PageRequest.of(0, 1, newestFirst));
        Slice<PublicationEntityJPA> secondSlice = springDataRepository.findByStatusAndMunicipality(
                PublicationStatusEntity.PUBLISHED, "05615", PageRequest.of(1, 1, newestFirst));

        assertEquals(List.of(newest.getId()), firstSlice.getContent().stream().map(PublicationEntityJPA::getId).toList());
        assertTrue(firstSlice.hasNext());
        assertEquals(List.of(oldest.getId()), secondSlice.getContent().stream().map(PublicationEntityJPA::getId).toList());
        assertFalse(secondSlice.hasNext());
    }

    @Test
    void shouldListPublishedPublicationsOfEveryMunicipalityWhenNoneIsSelected() {
        UUID ownerId = UUID.randomUUID();
        springDataRepository.saveAllAndFlush(List.of(
                toJpaEntity(publicationIn("05615", ownerId, "publications/d.jpg", "2026-10-18T12:00:00Z")),
                toJpaEntity(publicationIn("05440", ownerId, "publications/e.jpg", "2026-10-19T12:00:00Z"))
        ));

        Slice<PublicationEntityJPA> slice = springDataRepository.findByStatus(
                PublicationStatusEntity.PUBLISHED,
                PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")))
        );

        assertEquals(2, slice.getContent().size());
        assertEquals("05440", slice.getContent().get(0).getMunicipality());
    }

    private PublicationEntityJPA toJpaEntity(Publication publication) {
        return jpaMapper.toJpaEntity(domainMapper.toEntity(publication));
    }

    private Publication publication(UUID ownerId, String objectKey) {
        return Publication.publish(
                ownerId,
                "Ana",
                new Name("Bicicleta"),
                PublicationType.EXCHANGE,
                new Description("Bicicleta en buen estado"),
                new Municipality("05615"),
                List.of(new PublicationPhoto(UUID.randomUUID(), objectKey, 0)),
                Instant.parse("2026-10-18T12:00:00Z"),
                new Price(850_000)
        );
    }

    private Publication publicationIn(String municipalityCode, UUID ownerId, String objectKey, String createdAt) {
        return Publication.publish(
                ownerId,
                "Ana",
                new Name("Bicicleta"),
                PublicationType.EXCHANGE,
                new Description("Bicicleta en buen estado"),
                new Municipality(municipalityCode),
                List.of(new PublicationPhoto(UUID.randomUUID(), objectKey, 0)),
                Instant.parse(createdAt),
                new Price(850_000)
        );
    }
}
