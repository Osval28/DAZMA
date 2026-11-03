package co.dazma.api.publications.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PublicationRulesTest {

    @Test
    void shouldAcceptAConsistentConfiguration() {
        PublicationRules rules = new PublicationRules(7, 60, 15);

        assertEquals(7, rules.maximumPhotos());
        assertEquals(60, rules.maximumPageSize());
        assertEquals(15, rules.defaultPageSize());
    }

    @Test
    void shouldRejectNonPositiveLimitsAndADefaultAboveTheMaximum() {
        assertThrows(IllegalArgumentException.class, () -> new PublicationRules(0, 60, 15));
        assertThrows(IllegalArgumentException.class, () -> new PublicationRules(7, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> new PublicationRules(7, 60, 0));
        assertThrows(IllegalArgumentException.class, () -> new PublicationRules(7, 10, 11));
    }
}
