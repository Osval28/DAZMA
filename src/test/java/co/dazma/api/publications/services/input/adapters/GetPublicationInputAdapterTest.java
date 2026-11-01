package co.dazma.api.publications.services.input.adapters;

import co.dazma.api.publications.domain.Description;
import co.dazma.api.publications.domain.Name;
import co.dazma.api.publications.domain.Price;
import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationPhoto;
import co.dazma.api.publications.domain.PublicationStatus;
import co.dazma.api.publications.domain.PublicationType;
import co.dazma.api.publications.services.input.adapters.mappers.PublicationDetailMapper;
import co.dazma.api.publications.services.input.dto.GetPublicationOutputDto;
import co.dazma.api.publications.services.input.dto.GetPublicationPhotoOutputDto;
import co.dazma.api.publications.services.usecases.contract.GetPublicationUseCase;
import co.dazma.api.publications.services.usecases.models.PhotoAccess;
import co.dazma.api.publications.services.usecases.models.PublicationDetail;
import co.dazma.api.shared.domain.Municipality;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetPublicationInputAdapterTest {

    private static final Instant CREATED_AT = Instant.parse("2026-10-20T20:00:00Z");

    @Test
    void shouldExposeValueObjectsAsPlainValuesWithTheSignedPhotoUrls() {
        GetPublicationUseCase useCase = mock(GetPublicationUseCase.class);
        GetPublicationInputAdapter adapter = new GetPublicationInputAdapter(useCase, new PublicationDetailMapper());
        UUID photoId = UUID.randomUUID();
        URI signedUrl = URI.create("https://storage.example/publications/photo-0.jpg");
        Publication publication = Publication.restore(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Ana",
                new Name("Bicicleta"),
                PublicationType.EXCHANGE,
                new Description("Bicicleta de montaña en buen estado"),
                new Municipality("05615"),
                PublicationStatus.PUBLISHED,
                List.of(new PublicationPhoto(photoId, "publications/photo-0.jpg", 0)),
                CREATED_AT,
                new Price(900_000)
        );
        when(useCase.execute(publication.getId())).thenReturn(
                new PublicationDetail(publication, List.of(new PhotoAccess(photoId, signedUrl, 0)))
        );

        GetPublicationOutputDto output = adapter.execute(publication.getId());

        assertEquals(publication.getId(), output.id());
        assertEquals(publication.getOwnerId(), output.ownerId());
        assertEquals("Bicicleta", output.name());
        assertEquals("Bicicleta de montaña en buen estado", output.description());
        assertEquals("05615", output.municipality());
        assertEquals(900_000, output.price());
        assertEquals(PublicationType.EXCHANGE, output.type());
        assertEquals(PublicationStatus.PUBLISHED, output.status());
        assertEquals(CREATED_AT, output.createdAt());
        assertEquals(List.of(new GetPublicationPhotoOutputDto(photoId, signedUrl, 0)), output.photos());
    }
}
