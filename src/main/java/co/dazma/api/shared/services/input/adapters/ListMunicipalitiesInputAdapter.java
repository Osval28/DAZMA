package co.dazma.api.shared.services.input.adapters;

import co.dazma.api.shared.services.input.dto.MunicipalityOutputDto;
import co.dazma.api.shared.services.input.ports.ListMunicipalitiesInputPort;
import co.dazma.api.shared.services.usecases.contract.ListMunicipalitiesUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListMunicipalitiesInputAdapter implements ListMunicipalitiesInputPort {

    private final ListMunicipalitiesUseCase useCase;

    public ListMunicipalitiesInputAdapter(ListMunicipalitiesUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public List<MunicipalityOutputDto> execute() {
        return useCase.execute().stream()
                .map(m -> new MunicipalityOutputDto(m.municipality().code(), m.name(), m.department()))
                .toList();
    }
}
