package co.dazma.api.users.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @Test
    void shouldCreateAValidEmail() {
        assertEquals("ana@dazma.store", new Email("ana@dazma.store").value());
    }

    @Test
    void shouldRejectABlankEmail() {
        InvalidUserException exception = assertThrows(InvalidUserException.class, () -> new Email(" "));

        assertEquals(UserErrorCode.EMAIL_REQUIRED, exception.errorCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ana", "ana@dazma", "ana @dazma.store", "@dazma.store"})
    void shouldRejectAnEmailWithAnInvalidFormat(String value) {
        InvalidUserException exception = assertThrows(InvalidUserException.class, () -> new Email(value));

        assertEquals(UserErrorCode.EMAIL_INVALID, exception.errorCode());
    }
}
