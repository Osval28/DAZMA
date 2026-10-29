package co.dazma.api.shared.services.usecases.impl;

import co.dazma.api.shared.domain.CatalogedMunicipality;
import co.dazma.api.shared.domain.Municipality;
import co.dazma.api.shared.services.output.ports.MunicipalityCatalogPort;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListMunicipalitiesServiceTest {

    @Test
    void shouldListMunicipalitiesSortedByNameIgnoringAccents() {
        MunicipalityCatalogPort catalog = mock(MunicipalityCatalogPort.class);
        when(catalog.findAll()).thenReturn(List.of(
                new CatalogedMunicipality(new Municipality("05615"), "Rionegro", "Antioquia"),
                new CatalogedMunicipality(new Municipality("05021"), "Alejandría", "Antioquia"),
                new CatalogedMunicipality(new Municipality("05002"), "Abejorral", "Antioquia")
        ));

        List<CatalogedMunicipality> result = new ListMunicipalitiesService(catalog).execute();

        assertEquals(List.of("Abejorral", "Alejandría", "Rionegro"),
                result.stream().map(CatalogedMunicipality::name).toList());
    }
}
