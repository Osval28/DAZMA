package co.dazma.api.users.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhoneTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "3001234567",
            "300 123 4567",
            "300-123-4567",
            "(300) 123 4567",
            "+57 300 123 4567",
            "573001234567"
    })
    void shouldNormalizeAValidColombianMobile(String rawNumber) {
        assertEquals("3001234567", new Phone(rawNumber).number());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2001234567",
            "300123456",
            "30012345678",
            "300abc4567",
            "+1 300 123 4567"
    })
    void shouldRejectANumberThatIsNotAColombianMobile(String rawNumber) {
        InvalidUserException exception = assertThrows(
                InvalidUserException.class,
                () -> new Phone(rawNumber)
        );

        assertEquals("USER_PHONE_INVALID", exception.code());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void shouldRejectABlankNumber(String rawNumber) {
        InvalidUserException exception = assertThrows(
                InvalidUserException.class,
                () -> new Phone(rawNumber)
        );

        assertEquals("USER_PHONE_REQUIRED", exception.code());
    }
}
