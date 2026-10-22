package co.dazma.api.users.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NameTest {

    @Test
    void shouldTrimTheName() {
        assertEquals("Ana", new Name("  Ana  ").value());
    }

    @Test
    void shouldRejectABlankName() {
        InvalidUserException exception = assertThrows(
                InvalidUserException.class,
                () -> new Name("   ")
        );

        assertEquals("USER_NAME_REQUIRED", exception.code());
    }

    @Test
    void shouldAcceptANameWithTheMaximumLength() {
        assertEquals(255, new Name("a".repeat(255)).value().length());
    }

    @Test
    void shouldRejectANameLongerThanTheMaximumLength() {
        InvalidUserException exception = assertThrows(
                InvalidUserException.class,
                () -> new Name("a".repeat(256))
        );

        assertEquals("USER_NAME_TOO_LONG", exception.code());
        assertEquals(255, exception.arguments()[0]);
    }
}
