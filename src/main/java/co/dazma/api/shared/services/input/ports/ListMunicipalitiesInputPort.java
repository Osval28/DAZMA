package co.dazma.api.shared.services.input.ports;

import co.dazma.api.shared.services.input.dto.MunicipalityOutputDto;

import java.util.List;

public interface ListMunicipalitiesInputPort {

    List<MunicipalityOutputDto> execute();
}
