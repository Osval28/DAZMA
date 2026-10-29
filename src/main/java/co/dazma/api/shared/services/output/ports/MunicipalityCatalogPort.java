package co.dazma.api.shared.services.output.ports;

import co.dazma.api.shared.domain.CatalogedMunicipality;
import co.dazma.api.shared.domain.Municipality;

import java.util.List;

/**
 * Catálogo de municipios (shared kernel). Lo consumen Usuarios y Publicaciones para validar el código
 * y el endpoint público para listarlos.
 */
public interface MunicipalityCatalogPort {

    boolean exists(Municipality municipality);

    List<CatalogedMunicipality> findAll();
}
