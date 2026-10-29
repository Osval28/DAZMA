package co.dazma.api.shared.services.input.adapters;

import co.dazma.api.shared.domain.CatalogedMunicipality;
import co.dazma.api.shared.domain.Municipality;
import co.dazma.api.shared.services.input.dto.MunicipalityOutputDto;
import co.dazma.api.shared.services.usecases.contract.ListMunicipalitiesUseCase;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListMunicipalitiesInputAdapterTest {

    private final ListMunicipalitiesUseCase useCase = mock(ListMunicipalitiesUseCase.class);
    private final ListMunicipalitiesInputAdapter adapter = new ListMunicipalitiesInputAdapter(useCase);

    @Test
    void shouldExposeTheCodeNameAndDepartmentOfEachMunicipality() {
        when(useCase.execute()).thenReturn(List.of(
                new CatalogedMunicipality(new Municipality("05615"), "Rionegro", "Antioquia")
        ));

        List<MunicipalityOutputDto> result = adapter.execute();

        assertEquals(List.of(new MunicipalityOutputDto("05615", "Rionegro", "Antioquia")), result);
    }

    @Test
    void shouldReturnAnEmptyListWhenTheCatalogIsEmpty() {
        when(useCase.execute()).thenReturn(List.of());

        assertTrue(adapter.execute().isEmpty());
    }
}
