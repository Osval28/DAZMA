package co.dazma.api.publications.services.input.adapters;

import co.dazma.api.publications.services.input.adapters.mappers.ListPublicationsMapper;
import co.dazma.api.publications.services.input.dto.ListPublicationsInputDto;
import co.dazma.api.publications.services.input.dto.ListPublicationsOutputDto;
import co.dazma.api.publications.services.input.ports.ListPublicationsInputPort;
import co.dazma.api.publications.services.usecases.contract.ListPublicationsUseCase;
import org.springframework.stereotype.Service;

@Service
public class ListPublicationsInputAdapter implements ListPublicationsInputPort {

    private final ListPublicationsUseCase useCase;
    private final ListPublicationsMapper mapper;

    public ListPublicationsInputAdapter(ListPublicationsUseCase useCase, ListPublicationsMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @Override
    public ListPublicationsOutputDto execute(ListPublicationsInputDto input) {
        return mapper.toOutput(useCase.execute(mapper.toDomain(input)));
    }
}
