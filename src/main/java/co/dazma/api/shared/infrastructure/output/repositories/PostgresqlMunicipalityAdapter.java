package co.dazma.api.shared.infrastructure.output.repositories;

import co.dazma.api.shared.domain.CatalogedMunicipality;
import co.dazma.api.shared.domain.Municipality;
import co.dazma.api.shared.infrastructure.output.repositories.jpa.SpringDataMunicipalityRepository;
import co.dazma.api.shared.services.output.ports.MunicipalityCatalogPort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PostgresqlMunicipalityAdapter implements MunicipalityCatalogPort {

    private final SpringDataMunicipalityRepository repository;

    public PostgresqlMunicipalityAdapter(SpringDataMunicipalityRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(Municipality municipality) {
        return repository.existsById(municipality.code());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogedMunicipality> findAll() {
        return repository.findAll().stream()
                .map(e -> new CatalogedMunicipality(new Municipality(e.getCode()), e.getName(), e.getDepartment()))
                .toList();
    }
}
