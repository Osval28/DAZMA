package co.dazma.api.publications.infrastructure.output.parameters;

import co.dazma.api.publications.domain.PublicationRules;
import co.dazma.api.shared.services.output.ports.ParameterCatalog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CatalogPublicationRulesAdapterTest {

    @Test
    void shouldUseTheCatalogValuesForAllPublicationRules() {
        ParameterCatalog catalog = mock(ParameterCatalog.class);
        when(catalog.getInt(PublicationParameter.MAXIMUM_PHOTOS)).thenReturn(7);
        when(catalog.getInt(PublicationParameter.MAXIMUM_PAGE_SIZE)).thenReturn(60);
        when(catalog.getInt(PublicationParameter.DEFAULT_PAGE_SIZE)).thenReturn(15);

        PublicationRules rules = new CatalogPublicationRulesAdapter(catalog).current();

        assertEquals(new PublicationRules(7, 60, 15), rules);
    }

    @Test
    void shouldRejectInconsistentCatalogValues() {
        ParameterCatalog catalog = mock(ParameterCatalog.class);
        when(catalog.getInt(PublicationParameter.MAXIMUM_PHOTOS)).thenReturn(7);
        when(catalog.getInt(PublicationParameter.MAXIMUM_PAGE_SIZE)).thenReturn(10);
        when(catalog.getInt(PublicationParameter.DEFAULT_PAGE_SIZE)).thenReturn(15);

        assertThrows(IllegalArgumentException.class, () -> new CatalogPublicationRulesAdapter(catalog).current());
    }
}
