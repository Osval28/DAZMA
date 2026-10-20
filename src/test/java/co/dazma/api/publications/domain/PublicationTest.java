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
                "  Bicicleta de montaña  ",
                PublicationType.EXCHANGE,
                "  Bicicleta en buen estado  ",
                "  Rionegro  ",
                List.of(photo),
                CREATED_AT,
                PRICE
        );

        assertNotNull(publication.getId());
        assertEquals(ownerId, publication.getOwnerId());
        assertEquals("Bicicleta de montaña", publication.getName());
        assertEquals(PublicationType.EXCHANGE, publication.getType());
        assertEquals("Bicicleta en buen estado", publication.getDescription());
        assertEquals("Rionegro", publication.getCity());
        assertEquals(PublicationStatus.PUBLISHED, publication.getStatus());
        assertEquals(List.of(photo), publication.getPhotos());
        assertEquals(CREATED_AT, publication.getCreatedAt());
        assertEquals(PRICE, publication.getPrice());
    }

    @Test
    void shouldRejectMissingOwner() {
        InvalidPublicationException exception = assertThrows(
                InvalidPublicationException.class,
                () -> Publication.publish(
                        null,
                        "Bicicleta",
                        PublicationType.EXCHANGE,
                        "En buen estado",
                        "Rionegro",
                        List.of(photo(0)),
                        CREATED_AT,
                        PRICE
                )
        );

        assertEquals("PUBLICATION_OWNER_REQUIRED", exception.code());
        assertEquals("publication.owner.required", exception.messageKey());
    }

    @Test
    void shouldRejectBlankRequiredTextFields() {
        UUID ownerId = UUID.randomUUID();

        assertThrows(
                InvalidPublicationException.class,
                () -> Publication.publish(
                        ownerId,
                        " ",
                        PublicationType.EXCHANGE,
                        "Descripción",
                        "Rionegro",
                        List.of(photo(0)),
                        CREATED_AT,
                        PRICE
                )
        );
        assertThrows(
                InvalidPublicationException.class,
                () -> Publication.publish(
                        ownerId,
                        "Bicicleta",
                        PublicationType.EXCHANGE,
                        " ",
                        "Rionegro",
                        List.of(photo(0)),
                        CREATED_AT,
                        PRICE
                )
        );
        assertThrows(
                InvalidPublicationException.class,
                () -> Publication.publish(
                        ownerId,
                        "Bicicleta",
                        PublicationType.EXCHANGE,
                        "Descripción",
                        " ",
                        List.of(photo(0)),
                        CREATED_AT,
                        PRICE
                )
        );
    }

    @Test
    void shouldRejectMissingType() {
        assertThrows(
                InvalidPublicationException.class,
                () -> Publication.publish(
                        UUID.randomUUID(),
                        "Bicicleta",
                        null,
                        "Descripción",
                        "Rionegro",
                        List.of(photo(0)),
                        CREATED_AT,
                        PRICE
                )
        );
    }

    @Test
    void shouldRejectAnEmptyPhotoList() {
        assertThrows(
                InvalidPublicationException.class,
                () -> Publication.publish(
                        UUID.randomUUID(),
                        "Bicicleta",
                        PublicationType.EXCHANGE,
                        "Descripción",
                        "Rionegro",
                        List.of(),
                        CREATED_AT,
                        PRICE
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
    void shouldRejectMissingCreationDate() {
        assertThrows(
                InvalidPublicationException.class,
                () -> Publication.publish(
                        UUID.randomUUID(),
                        "Bicicleta",
                        PublicationType.EXCHANGE,
                        "Descripción",
                        "Rionegro",
                        List.of(photo(0)),
                        null,
                        PRICE
                )
        );
    }

    @Test
    void shouldRejectMissingOrNonPositivePrice() {
        for (Integer invalidPrice : new Integer[]{null, 0, -1}) {
            assertThrows(
                    InvalidPublicationException.class,
                    () -> Publication.publish(
                            UUID.randomUUID(),
                            "Bicicleta",
                            PublicationType.EXCHANGE,
                            "Descripción",
                            "Rionegro",
                            List.of(photo(0)),
                            CREATED_AT,
                            invalidPrice
                    )
            );
        }
    }

    private Publication validPublication(List<PublicationPhoto> photos) {
        return Publication.publish(
                UUID.randomUUID(),
                "Bicicleta",
                PublicationType.EXCHANGE,
                "Descripción",
                "Rionegro",
                photos,
                CREATED_AT,
                PRICE
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
