package co.dazma.api.users.domain;

import co.dazma.api.users.domain.exceptions.InvalidUserException;
import co.dazma.api.users.domain.models.Phone;
import co.dazma.api.users.domain.models.PhoneRules;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhoneTest {

    @Test
    void shouldUseTheConfiguredCountryCodeForNewInput() {
        assertEquals("3001234567", Phone.fromInput("+58 300 123 4567", new PhoneRules("58", 10, "3")).number());
        assertEquals("USER_PHONE_INVALID", assertThrows(
                InvalidUserException.class,
                () -> Phone.fromInput("+57 300 123 4567", new PhoneRules("58", 10, "3"))
        ).code());
    }

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
        assertEquals("3001234567", Phone.fromInput(rawNumber, new PhoneRules("57", 10, "3")).number());
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
                () -> Phone.fromInput(rawNumber, new PhoneRules("57", 10, "3"))
        );

        assertEquals("USER_PHONE_INVALID", exception.code());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void shouldRejectABlankNumber(String rawNumber) {
        InvalidUserException exception = assertThrows(
                InvalidUserException.class,
                () -> Phone.fromInput(rawNumber, new PhoneRules("57", 10, "3"))
        );

        assertEquals("USER_PHONE_REQUIRED", exception.code());
    }

    @Test
    void shouldApplyAChangedLengthAndMobilePrefixWithoutInvalidatingStoredPhones() {
        PhoneRules changed = new PhoneRules("57", 9, "4");
        assertEquals("400123456", Phone.fromInput("+57 400 123 456", changed).number());
        assertEquals("3001234567", new Phone("3001234567").number());
        assertEquals("USER_PHONE_INVALID", assertThrows(InvalidUserException.class,
                () -> Phone.fromInput("3001234567", changed)).code());
    }
}
