package co.dazma.api.shared.infrastructure.output.repositories;

import co.dazma.api.TestcontainersConfiguration;
import co.dazma.api.shared.domain.Municipality;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class PostgresMunicipalityRepositoryIntegrationTest {

    @Autowired
    private PostgresqlMunicipalityAdapter adapter;

    @Test
    void shouldFindRionegroInTheLoadedCatalog() {
        assertTrue(adapter.exists(new Municipality("05615")));
    }

    @Test
    void shouldNotFindAMunicipalityOutsideTheCatalog() {
        assertFalse(adapter.exists(new Municipality("11001")));
    }

    @Test
    void shouldLoadTheTwentyThreeMunicipalitiesOfTheEasternSubregion() {
        assertEquals(23, adapter.findAll().size());
    }
}
