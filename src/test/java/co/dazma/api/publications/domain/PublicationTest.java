package co.dazma.api.publications.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PublicationTest {

    private static final Instant CREATED_AT = Instant.parse("2026-10-18T12:00:00Z");
    private static final int PRICE = 850_000;

    @Test
    void shouldPublishAValidPublication() {
        UUID ownerId = UUID.randomUUID();
        PublicationPhoto photo = photo(0);

        Publication publication = Publication.publish(
                ownerId,
                new Name("  Bicicleta de montaña  "),
                PublicationType.EXCHANGE,
                new Description("  Bicicleta en buen estado  "),
                new Municipality("  Rionegro  "),
                List.of(photo),
                CREATED_AT,
                new Price(PRICE)
        );

        assertNotNull(publication.getId());
        assertEquals(ownerId, publication.getOwnerId());
        assertEquals(new Name("Bicicleta de montaña"), publication.getName());
        assertEquals(PublicationType.EXCHANGE, publication.getType());
        assertEquals(new Description("Bicicleta en buen estado"), publication.getDescription());
        assertEquals(new Municipality("Rionegro"), publication.getMunicipality());
        assertEquals(PublicationStatus.PUBLISHED, publication.getStatus());
        assertEquals(List.of(photo), publication.getPhotos());
        assertEquals(CREATED_AT, publication.getCreatedAt());
        assertEquals(new Price(PRICE), publication.getPrice());
    }

    @Test
    void shouldRejectBlankRequiredTextFields() {
        UUID ownerId = UUID.randomUUID();

        assertThrows(
                InvalidPublicationException.class,
                () -> Publication.publish(
                        ownerId,
                        new Name(" "),
                        PublicationType.EXCHANGE,
                        new Description("Descripción"),
                        new Municipality("Rionegro"),
                        List.of(photo(0)),
                        CREATED_AT,
                        new Price(PRICE)
                )
        );
        assertThrows(
                InvalidPublicationException.class,
                () -> Publication.publish(
                        ownerId,
                        new Name("Bicicleta"),
                        PublicationType.EXCHANGE,
                        new Description(" "),
                        new Municipality("Rionegro"),
                        List.of(photo(0)),
                        CREATED_AT,
                        new Price(PRICE)
                )
        );
        assertThrows(
                InvalidPublicationException.class,
                () -> Publication.publish(
                        ownerId,
                        new Name("Bicicleta"),
                        PublicationType.EXCHANGE,
                        new Description("Descripción"),
                        new Municipality(" "),
                        List.of(photo(0)),
                        CREATED_AT,
                        new Price(PRICE)
                )
        );
    }

    @Test
    void shouldRejectAnEmptyPhotoList() {
        assertThrows(
                InvalidPublicationException.class,
                () -> Publication.publish(
                        UUID.randomUUID(),
                        new Name("Bicicleta"),
                        PublicationType.EXCHANGE,
                        new Description("Descripción"),
                        new Municipality("Rionegro"),
                        List.of(),
                        CREATED_AT,
                        new Price(PRICE)
                )
        );
    }

    @Test
    void shouldRejectDuplicatedPhotoIds() {
        UUID repeatedId = UUID.randomUUID();
        PublicationPhoto firstPhoto = new PublicationPhoto(repeatedId, "one.jpg", 0);
        PublicationPhoto repeatedPhoto = new PublicationPhoto(repeatedId, "two.jpg", 1);

        assertThrows(
                InvalidPublicationException.class,
                () -> validPublication(List.of(firstPhoto, repeatedPhoto))
        );
    }

    @Test
    void shouldRejectDuplicatedPhotoPositions() {
        PublicationPhoto firstPhoto = new PublicationPhoto(UUID.randomUUID(), "one.jpg", 0);
        PublicationPhoto secondPhoto = new PublicationPhoto(UUID.randomUUID(), "two.jpg", 0);

        assertThrows(
                InvalidPublicationException.class,
                () -> validPublication(List.of(firstPhoto, secondPhoto))
        );
    }

    @Test
    void shouldRejectInvalidPhotoData() {
        assertThrows(
                InvalidPublicationException.class,
                () -> new PublicationPhoto(null, "one.jpg", 0)
        );
        assertThrows(
                InvalidPublicationException.class,
                () -> new PublicationPhoto(UUID.randomUUID(), " ", 0)
        );
        assertThrows(
                InvalidPublicationException.class,
                () -> new PublicationPhoto(UUID.randomUUID(), "one.jpg", -1)
        );
    }

    @Test
    void shouldRejectNonPositivePrice() {
        for (Integer invalidPrice : new Integer[]{0, -1}) {
            assertThrows(InvalidPublicationException.class, () -> new Price(invalidPrice));
        }
    }

    @Test
    void shouldAcceptANameWithTheMaximumLength() {
        Name name = new Name("a".repeat(255));

        assertEquals(255, name.name().length());
    }

    @Test
    void shouldRejectANameLongerThanTheMaximumLength() {
        String tooLongName = "a".repeat(256);

        InvalidPublicationException exception = assertThrows(
                InvalidPublicationException.class,
                () -> new Name(tooLongName)
        );

        assertEquals("PUBLICATION_NAME_TOO_LONG", exception.code());
        assertEquals(255, exception.arguments()[0]);
    }

    @Test
    void shouldAcceptADescriptionWithTheMaximumLength() {
        Description description = new Description("a".repeat(1000));

        assertEquals(1000, description.text().length());
    }

    @Test
    void shouldRejectADescriptionLongerThanTheMaximumLength() {
        String tooLongDescription = "a".repeat(1001);

        InvalidPublicationException exception = assertThrows(
                InvalidPublicationException.class,
                () -> new Description(tooLongDescription)
        );

        assertEquals("PUBLICATION_DESCRIPTION_TOO_LONG", exception.code());
        assertEquals(1000, exception.arguments()[0]);
    }

    private Publication validPublication(List<PublicationPhoto> photos) {
        return Publication.publish(
                UUID.randomUUID(),
                new Name("Bicicleta"),
                PublicationType.EXCHANGE,
                new Description("Descripción"),
                new Municipality("Rionegro"),
                photos,
                CREATED_AT,
                new Price(PRICE)
        );
    }

    private PublicationPhoto photo(int position) {
        return new PublicationPhoto(
                UUID.randomUUID(),
                "publications/photo-" + position + ".jpg",
                position
        );
    }
}
