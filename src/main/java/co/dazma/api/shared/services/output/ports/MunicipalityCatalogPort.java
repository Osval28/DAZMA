package co.dazma.api.shared.services.output.ports;

import co.dazma.api.shared.domain.models.CatalogedMunicipality;
import co.dazma.api.shared.domain.models.Municipality;

import java.util.List;

public interface MunicipalityCatalogPort {

    boolean exists(Municipality municipality);

    List<CatalogedMunicipality> findAll();
}
