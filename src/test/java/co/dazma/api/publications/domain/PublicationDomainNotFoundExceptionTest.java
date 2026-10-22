package co.dazma.api.publications.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PublicationDomainNotFoundExceptionTest {

    @Test
    void shouldExposeItsCodeMessageKeyAndPublicationId() {
        UUID publicationId = UUID.randomUUID();

        PublicationNotFoundException exception = new PublicationNotFoundException(publicationId);

        assertEquals("PUBLICATION_NOT_FOUND", exception.code());
        assertEquals("publication.not-found", exception.messageKey());
        assertArrayEquals(new Object[]{publicationId}, exception.arguments());
    }
}
