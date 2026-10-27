package co.dazma.api.publications.services.input.adapters;

import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.services.input.adapters.mappers.CreatePublicationMapper;
import co.dazma.api.publications.services.input.dto.CreatePublicationInputDto;
import co.dazma.api.publications.services.input.dto.CreatePublicationOutputDto;
import co.dazma.api.publications.services.input.ports.CreatePublicationInputPort;
import co.dazma.api.publications.services.usecases.contract.CreatePublicationUseCase;
import org.springframework.stereotype.Service;

@Service
public class CreatePublicationInputAdapter implements CreatePublicationInputPort {

    private final CreatePublicationUseCase useCase;
    private final CreatePublicationMapper mapper;

    public CreatePublicationInputAdapter(CreatePublicationUseCase useCase, CreatePublicationMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @Override
    public CreatePublicationOutputDto execute(CreatePublicationInputDto input) {
        Publication publication = useCase.execute(mapper.toDomain(input));
        return mapper.toOutput(publication);
    }
}
