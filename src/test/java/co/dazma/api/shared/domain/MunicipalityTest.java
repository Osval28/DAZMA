package co.dazma.api.shared.domain;

import co.dazma.api.shared.domain.errors.InvalidMunicipalityException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MunicipalityTest {

    @Test
    void shouldTrimTheName() {
        assertEquals("Rionegro", new Municipality("  Rionegro  ").name());
    }

    @Test
    void shouldRejectABlankName() {
        InvalidMunicipalityException exception = assertThrows(
                InvalidMunicipalityException.class,
                () -> new Municipality("   ")
        );

        assertEquals("MUNICIPALITY_REQUIRED", exception.code());
    }

    @Test
    void shouldAcceptANameWithTheMaximumLength() {
        assertEquals(255, new Municipality("a".repeat(255)).name().length());
    }

    @Test
    void shouldRejectANameLongerThanTheMaximumLength() {
        InvalidMunicipalityException exception = assertThrows(
                InvalidMunicipalityException.class,
                () -> new Municipality("a".repeat(256))
        );

        assertEquals("MUNICIPALITY_TOO_LONG", exception.code());
        assertEquals(255, exception.arguments()[0]);
    }
}
