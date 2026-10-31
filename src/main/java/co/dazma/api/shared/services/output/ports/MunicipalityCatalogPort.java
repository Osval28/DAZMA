package co.dazma.api.shared.services.output.ports;

import co.dazma.api.shared.domain.CatalogedMunicipality;
import co.dazma.api.shared.domain.Municipality;

import java.util.List;

public interface MunicipalityCatalogPort {

    boolean exists(Municipality municipality);

    List<CatalogedMunicipality> findAll();
}
