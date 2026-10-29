package co.dazma.api.shared.infrastructure.output.repositories;

import co.dazma.api.shared.domain.CatalogedMunicipality;
import co.dazma.api.shared.domain.Municipality;
import co.dazma.api.shared.infrastructure.output.repositories.jpa.MunicipalityEntityJPA;
import co.dazma.api.shared.infrastructure.output.repositories.jpa.SpringDataMunicipalityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostgresqlMunicipalityAdapterTest {

    private SpringDataMunicipalityRepository repository;
    private PostgresqlMunicipalityAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(SpringDataMunicipalityRepository.class);
        adapter = new PostgresqlMunicipalityAdapter(repository);
    }

    @Test
    void shouldReportThatACatalogedMunicipalityExists() {
        when(repository.existsById("05615")).thenReturn(true);

        assertTrue(adapter.exists(new Municipality("05615")));
    }

    @Test
    void shouldReportThatAMunicipalityOutsideTheCatalogDoesNotExist() {
        when(repository.existsById("99999")).thenReturn(false);

        assertFalse(adapter.exists(new Municipality("99999")));
    }

    @Test
    void shouldMapEveryStoredRowToACatalogedMunicipality() {
        MunicipalityEntityJPA entity = mock(MunicipalityEntityJPA.class);
        when(entity.getCode()).thenReturn("05615");
        when(entity.getName()).thenReturn("Rionegro");
        when(entity.getDepartment()).thenReturn("Antioquia");
        when(repository.findAll()).thenReturn(List.of(entity));

        List<CatalogedMunicipality> result = adapter.findAll();

        assertEquals(
                List.of(new CatalogedMunicipality(new Municipality("05615"), "Rionegro", "Antioquia")),
                result
        );
    }
}
