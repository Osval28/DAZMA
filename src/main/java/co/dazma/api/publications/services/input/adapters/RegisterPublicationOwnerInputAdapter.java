package co.dazma.api.publications.services.input.adapters;

import co.dazma.api.publications.services.input.adapters.mappers.PublicationOwnerMapper;
import co.dazma.api.publications.services.input.dto.RegisterPublicationOwnerInputDto;
import co.dazma.api.publications.services.input.ports.RegisterPublicationOwnerInputPort;
import co.dazma.api.publications.services.usecases.contract.RegisterPublicationOwnerUseCase;
import org.springframework.stereotype.Service;

@Service
public class RegisterPublicationOwnerInputAdapter implements RegisterPublicationOwnerInputPort {

    private final RegisterPublicationOwnerUseCase useCase;
    private final PublicationOwnerMapper mapper;

    public RegisterPublicationOwnerInputAdapter(RegisterPublicationOwnerUseCase useCase, PublicationOwnerMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @Override
    public void execute(RegisterPublicationOwnerInputDto input) {
        useCase.execute(mapper.toDomain(input));
    }
}
