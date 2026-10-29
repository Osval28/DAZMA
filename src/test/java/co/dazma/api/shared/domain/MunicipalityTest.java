package co.dazma.api.shared.domain;

import co.dazma.api.shared.domain.errors.InvalidMunicipalityException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MunicipalityTest {

    @Test
    void shouldTrimTheCode() {
        assertEquals("05615", new Municipality("  05615  ").code());
    }

    @Test
    void shouldRejectABlankCode() {
        InvalidMunicipalityException exception = assertThrows(
                InvalidMunicipalityException.class,
                () -> new Municipality("   ")
        );

        assertEquals("MUNICIPALITY_REQUIRED", exception.code());
    }

    @Test
    void shouldRejectACodeThatIsNotFiveDigits() {
        for (String invalid : new String[]{"Rionegro", "5615", "056150", "0561a", "05 15"}) {
            InvalidMunicipalityException exception = assertThrows(
                    InvalidMunicipalityException.class,
                    () -> new Municipality(invalid),
                    "Debía rechazar: " + invalid
            );

            assertEquals("MUNICIPALITY_INVALID_FORMAT", exception.code());
        }
    }
}
