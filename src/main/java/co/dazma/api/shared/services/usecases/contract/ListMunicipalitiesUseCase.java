package co.dazma.api.shared.services.usecases.contract;

import co.dazma.api.shared.domain.CatalogedMunicipality;

import java.util.List;

public interface ListMunicipalitiesUseCase {

    List<CatalogedMunicipality> execute();
}
