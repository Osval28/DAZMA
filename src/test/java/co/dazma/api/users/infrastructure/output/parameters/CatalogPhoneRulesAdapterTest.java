package co.dazma.api.users.infrastructure.output.parameters;

import co.dazma.api.shared.services.output.ports.ParameterCatalog;
import co.dazma.api.users.domain.models.PhoneRules;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CatalogPhoneRulesAdapterTest {

    @Test
    void shouldReadPhoneRulesFromTheCatalog() {
        ParameterCatalog catalog = mock(ParameterCatalog.class);
        when(catalog.getText(UserParameter.PHONE_COUNTRY_CODE)).thenReturn("58");
        when(catalog.getInt(UserParameter.PHONE_NATIONAL_LENGTH)).thenReturn(9);
        when(catalog.getText(UserParameter.PHONE_MOBILE_PREFIX)).thenReturn("4");

        assertEquals(new PhoneRules("58", 9, "4"), new CatalogPhoneRulesAdapter(catalog).current());
    }

    @Test
    void shouldRejectAnInvalidCatalogConfiguration() {
        ParameterCatalog catalog = mock(ParameterCatalog.class);
        when(catalog.getText(UserParameter.PHONE_COUNTRY_CODE)).thenReturn("57");
        when(catalog.getInt(UserParameter.PHONE_NATIONAL_LENGTH)).thenReturn(0);
        when(catalog.getText(UserParameter.PHONE_MOBILE_PREFIX)).thenReturn("3");

        assertThrows(IllegalArgumentException.class, () -> new CatalogPhoneRulesAdapter(catalog).current());
    }
}
