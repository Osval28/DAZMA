package co.dazma.api.users.services.input.adapters;

import co.dazma.api.users.domain.models.PhoneRules;
import co.dazma.api.users.services.input.dto.PhoneRulesOutputDto;
import co.dazma.api.users.services.output.ports.PhoneRulesPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetPhoneRulesInputAdapterTest {

    @Test
    void shouldExposeTheCurrentPhoneRules() {
        PhoneRulesPort rules = mock(PhoneRulesPort.class);
        when(rules.current()).thenReturn(new PhoneRules("58", 9, "4"));

        PhoneRulesOutputDto result = new GetPhoneRulesInputAdapter(rules).execute();

        assertEquals("58", result.countryCode());
        assertEquals(9, result.nationalLength());
        assertEquals("4", result.mobilePrefix());
    }
}
