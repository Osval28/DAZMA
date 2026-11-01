package co.dazma.api.publications.services.input.adapters;

import co.dazma.api.publications.domain.Description;
import co.dazma.api.publications.domain.InvalidPublicationException;
import co.dazma.api.publications.domain.Name;
import co.dazma.api.publications.domain.Price;
import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationDraft;
import co.dazma.api.publications.domain.PublicationPhoto;
import co.dazma.api.publications.domain.PublicationStatus;
import co.dazma.api.publications.domain.PublicationType;
import co.dazma.api.publications.services.input.adapters.mappers.CreatePublicationMapper;
import co.dazma.api.publications.services.input.dto.CreatePublicationInputDto;
import co.dazma.api.publications.services.input.dto.CreatePublicationOutputDto;
import co.dazma.api.publications.services.usecases.contract.CreatePublicationUseCase;
import co.dazma.api.shared.domain.Municipality;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CreatePublicationInputAdapterTest {

    private static final Instant NOW = Instant.parse("2026-10-20T20:00:00Z");

    private CreatePublicationUseCase useCase;
    private CreatePublicationInputAdapter adapter;

    @BeforeEach
    void setUp() {
        useCase = mock(CreatePublicationUseCase.class);
        adapter = new CreatePublicationInputAdapter(useCase, new CreatePublicationMapper());
    }

    @Test
    void shouldGiveTheUseCaseADraftOfValueObjectsAndReturnTheCreatedPublication() {
        UUID photoId = UUID.randomUUID();
        PublicationDraft expectedDraft = new PublicationDraft(
                "firebase-123",
                true,
                new Name("Bicicleta"),
                PublicationType.EXCHANGE,
                new Description("Bicicleta de montaña en buen estado"),
                new Municipality("05615"),
                new Price(900_000),
                List.of(photoId)
        );
        Publication created = Publication.publish(
                UUID.randomUUID(),
                "Ana",
                new Name("Bicicleta"),
                PublicationType.EXCHANGE,
                new Description("Bicicleta de montaña en buen estado"),
                new Municipality("05615"),
                List.of(new PublicationPhoto(photoId, "publications/photo-0.jpg", 0)),
                NOW,
                new Price(900_000)
        );
        when(useCase.execute(expectedDraft)).thenReturn(created);

        CreatePublicationOutputDto output = adapter.execute(input("Bicicleta", List.of(photoId)));

        assertEquals(new CreatePublicationOutputDto(created.getId(), PublicationStatus.PUBLISHED, NOW), output);
    }

    @Test
    void shouldNotCallTheUseCaseWhenAFieldIsInvalid() {
        assertThrows(
                InvalidPublicationException.class,
                () -> adapter.execute(input("   ", List.of(UUID.randomUUID())))
        );

        verifyNoInteractions(useCase);
    }

    private static CreatePublicationInputDto input(String name, List<UUID> photoIds) {
        return new CreatePublicationInputDto(
                "firebase-123",
                true,
                name,
                PublicationType.EXCHANGE,
                "Bicicleta de montaña en buen estado",
                "05615",
                900_000,
                photoIds
        );
    }
}
