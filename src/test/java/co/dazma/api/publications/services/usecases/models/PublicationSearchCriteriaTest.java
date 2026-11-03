package co.dazma.api.publications.services.usecases.models;

import co.dazma.api.publications.domain.InvalidPublicationException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PublicationSearchCriteriaTest {

    private static final int MAX_SIZE = 50;

    @Test
    void shouldAcceptTheFirstPageAndTheMaximumSize() {
        assertDoesNotThrow(() -> new PublicationSearchCriteria(Optional.empty(), 0, MAX_SIZE, MAX_SIZE));
    }

    @Test
    void shouldRejectANegativePage() {
        InvalidPublicationException exception = assertThrows(
                InvalidPublicationException.class,
                () -> new PublicationSearchCriteria(Optional.empty(), -1, 20, MAX_SIZE)
        );

        assertEquals("PUBLICATION_PAGE_INVALID", exception.code());
    }

    @Test
    void shouldRejectASizeOutsideTheAllowedRange() {
        for (int invalidSize : new int[]{0, -5, MAX_SIZE + 1}) {
            InvalidPublicationException exception = assertThrows(
                    InvalidPublicationException.class,
                    () -> new PublicationSearchCriteria(Optional.empty(), 0, invalidSize, MAX_SIZE)
            );

            assertEquals("PUBLICATION_PAGE_SIZE_INVALID", exception.code());
            assertEquals(MAX_SIZE, exception.arguments()[0]);
        }
    }
}
