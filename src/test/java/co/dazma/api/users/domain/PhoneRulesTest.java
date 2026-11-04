package co.dazma.api.users.domain;

import co.dazma.api.users.domain.models.PhoneRules;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhoneRulesTest {

    @Test
    void shouldAcceptAConsistentConfiguration() {
        PhoneRules rules = new PhoneRules("57", 10, "3");

        assertEquals("57", rules.countryCode());
        assertEquals(10, rules.nationalLength());
        assertEquals("3", rules.mobilePrefix());
    }

    @Test
    void shouldRejectAnInvalidCountryCode() {
        assertThrows(IllegalArgumentException.class, () -> new PhoneRules(null, 10, "3"));
        assertThrows(IllegalArgumentException.class, () -> new PhoneRules("0", 10, "3"));
        assertThrows(IllegalArgumentException.class, () -> new PhoneRules("1234", 10, "3"));
        assertThrows(IllegalArgumentException.class, () -> new PhoneRules("5a", 10, "3"));
    }

    @Test
    void shouldRejectAnInvalidNationalLength() {
        assertThrows(IllegalArgumentException.class, () -> new PhoneRules("57", 0, "3"));
        assertThrows(IllegalArgumentException.class, () -> new PhoneRules("57", 14, "3"));
    }

    @Test
    void shouldRejectAnInvalidMobilePrefix() {
        assertThrows(IllegalArgumentException.class, () -> new PhoneRules("57", 10, null));
        assertThrows(IllegalArgumentException.class, () -> new PhoneRules("57", 10, ""));
        assertThrows(IllegalArgumentException.class, () -> new PhoneRules("57", 10, "3a"));
        assertThrows(IllegalArgumentException.class, () -> new PhoneRules("57", 2, "300"));
    }
}
