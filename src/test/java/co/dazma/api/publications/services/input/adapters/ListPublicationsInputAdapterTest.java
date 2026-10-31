package co.dazma.api.publications.services.input.adapters;

import co.dazma.api.publications.domain.Description;
import co.dazma.api.publications.domain.Name;
import co.dazma.api.publications.domain.Price;
import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationPhoto;
import co.dazma.api.publications.domain.PublicationStatus;
import co.dazma.api.publications.domain.PublicationType;
import co.dazma.api.publications.services.input.adapters.mappers.ListPublicationsMapper;
import co.dazma.api.publications.services.input.dto.ListPublicationsInputDto;
import co.dazma.api.publications.services.input.dto.ListPublicationsOutputDto;
import co.dazma.api.publications.services.usecases.contract.ListPublicationsUseCase;
import co.dazma.api.publications.services.usecases.models.PhotoAccess;
import co.dazma.api.publications.services.usecases.models.PublicationSearchCriteria;
import co.dazma.api.publications.services.usecases.models.PublicationSummary;
import co.dazma.api.publications.services.usecases.models.PublicationSummaryPage;
import co.dazma.api.shared.domain.Municipality;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListPublicationsInputAdapterTest {

    private ListPublicationsUseCase useCase;
    private ListPublicationsInputAdapter adapter;

    @BeforeEach
    void setUp() {
        useCase = mock(ListPublicationsUseCase.class);
        adapter = new ListPublicationsInputAdapter(useCase, new ListPublicationsMapper());
        when(useCase.execute(any())).thenReturn(new PublicationSummaryPage(List.of(), 0, 20, false));
    }

    @Test
    void shouldFilterByMunicipalityWhenOneIsSent() {
        adapter.execute(new ListPublicationsInputDto(Optional.of("05615"), 1, 5));

        ArgumentCaptor<PublicationSearchCriteria> criteria = ArgumentCaptor.forClass(PublicationSearchCriteria.class);
        verify(useCase).execute(criteria.capture());
        assertEquals(Optional.of(new Municipality("05615")), criteria.getValue().municipality());
        assertEquals(1, criteria.getValue().page());
        assertEquals(5, criteria.getValue().size());
    }

    @Test
    void shouldTreatABlankMunicipalityAsNoFilter() {
        adapter.execute(new ListPublicationsInputDto(Optional.of("  "), 0, 20));

        ArgumentCaptor<PublicationSearchCriteria> criteria = ArgumentCaptor.forClass(PublicationSearchCriteria.class);
        verify(useCase).execute(criteria.capture());
        assertTrue(criteria.getValue().municipality().isEmpty());
    }

    @Test
    void shouldExposeEachSummaryAsPlainValuesWithoutTheOwner() {
        UUID photoId = UUID.randomUUID();
        URI cover = URI.create("https://storage.example/publications/photo-0.jpg");
        Publication publication = Publication.restore(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new Name("Bicicleta"),
                PublicationType.EXCHANGE,
                new Description("Bicicleta de montaña en buen estado"),
                new Municipality("05615"),
                PublicationStatus.PUBLISHED,
                List.of(new PublicationPhoto(photoId, "publications/photo-0.jpg", 0)),
                Instant.parse("2026-10-20T20:00:00Z"),
                new Price(900_000)
        );
        when(useCase.execute(any())).thenReturn(new PublicationSummaryPage(
                List.of(new PublicationSummary(publication, new PhotoAccess(photoId, cover, 0))),
                3,
                20,
                true
        ));

        ListPublicationsOutputDto output = adapter.execute(new ListPublicationsInputDto(Optional.empty(), 3, 20));

        assertEquals(3, output.page());
        assertEquals(20, output.size());
        assertTrue(output.hasNext());
        assertEquals(1, output.items().size());
        assertEquals(publication.getId(), output.items().get(0).id());
        assertEquals("Bicicleta", output.items().get(0).name());
        assertEquals(PublicationType.EXCHANGE, output.items().get(0).type());
        assertEquals("05615", output.items().get(0).municipality());
        assertEquals(900_000, output.items().get(0).price());
        assertEquals(cover, output.items().get(0).coverUrl());
        assertEquals(Instant.parse("2026-10-20T20:00:00Z"), output.items().get(0).createdAt());
    }
}
