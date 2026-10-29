package co.dazma.api.publications.infrastructure.output.repositories.structured;

import co.dazma.api.publications.domain.Description;
import co.dazma.api.publications.domain.Name;
import co.dazma.api.publications.domain.Price;
import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationPhoto;
import co.dazma.api.publications.domain.PublicationType;
import co.dazma.api.publications.infrastructure.output.repositories.structured.entities.PublicationStatusEntity;
import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.entities.PublicationEntityJPA;
import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.repository.SpringDataPublicationRepository;
import co.dazma.api.publications.infrastructure.output.repositories.structured.mapper.PublicationMapStructDomainMapper;
import co.dazma.api.publications.infrastructure.output.repositories.structured.mapper.PublicationMapStructJpaMapper;
import co.dazma.api.publications.services.output.models.PublishedPublicationsSlice;
import co.dazma.api.shared.domain.Municipality;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostgresqlPublicationAdapterTest {

    private SpringDataPublicationRepository springDataRepository;
    private PublicationMapStructDomainMapper domainMapper;
    private PublicationMapStructJpaMapper jpaMapper;
    private PostgresqlPublicationAdapter adapter;

    @BeforeEach
    void setUp() {
        springDataRepository = mock(SpringDataPublicationRepository.class);
        domainMapper = Mappers.getMapper(PublicationMapStructDomainMapper.class);
        jpaMapper = Mappers.getMapper(PublicationMapStructJpaMapper.class);
        adapter = new PostgresqlPublicationAdapter(springDataRepository, domainMapper, jpaMapper);
    }

    @Test
    void shouldQueryTheMunicipalityWhenOneIsSelected() {
        Publication publication = publication("05615");
        when(springDataRepository.findByStatusAndMunicipality(
                eq(PublicationStatusEntity.PUBLISHED), eq("05615"), any(Pageable.class)))
                .thenReturn(new SliceImpl<>(List.of(toJpa(publication)), PageRequest.of(1, 10), true));

        PublishedPublicationsSlice result = adapter.findPublished(Optional.of(new Municipality("05615")), 1, 10);

        assertEquals(List.of(publication.getId()), result.publications().stream().map(Publication::getId).toList());
        assertTrue(result.hasNext());
        verify(springDataRepository, never()).findByStatus(any(), any());
    }

    @Test
    void shouldQueryEveryMunicipalityWhenNoneIsSelected() {
        Publication publication = publication("05440");
        when(springDataRepository.findByStatus(eq(PublicationStatusEntity.PUBLISHED), any(Pageable.class)))
                .thenReturn(new SliceImpl<>(List.of(toJpa(publication))));

        PublishedPublicationsSlice result = adapter.findPublished(Optional.empty(), 0, 20);

        assertEquals(1, result.publications().size());
        assertFalse(result.hasNext());
        verify(springDataRepository, never()).findByStatusAndMunicipality(any(), any(), any());
    }

    @Test
    void shouldRequestTheNewestFirstWithTheGivenPageAndSize() {
        when(springDataRepository.findByStatus(any(), any(Pageable.class))).thenReturn(new SliceImpl<>(List.of()));

        adapter.findPublished(Optional.empty(), 3, 15);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(springDataRepository).findByStatus(eq(PublicationStatusEntity.PUBLISHED), captor.capture());
        assertEquals(3, captor.getValue().getPageNumber());
        assertEquals(15, captor.getValue().getPageSize());
        assertEquals(
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")),
                captor.getValue().getSort()
        );
    }

    private PublicationEntityJPA toJpa(Publication publication) {
        return jpaMapper.toJpaEntity(domainMapper.toEntity(publication));
    }

    private Publication publication(String municipalityCode) {
        return Publication.publish(
                UUID.randomUUID(),
                new Name("Bicicleta"),
                PublicationType.EXCHANGE,
                new Description("Bicicleta en buen estado"),
                new Municipality(municipalityCode),
                List.of(new PublicationPhoto(UUID.randomUUID(), "publications/a.jpg", 0)),
                Instant.parse("2026-10-18T12:00:00Z"),
                new Price(850_000)
        );
    }
}
