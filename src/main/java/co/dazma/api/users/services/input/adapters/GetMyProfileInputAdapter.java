package co.dazma.api.users.services.input.adapters;

import co.dazma.api.users.domain.User;
import co.dazma.api.users.services.input.adapters.mappers.MyProfileMapper;
import co.dazma.api.users.services.input.dto.GetMyProfileInputDto;
import co.dazma.api.users.services.input.dto.GetMyProfileOutputDto;
import co.dazma.api.users.services.input.ports.GetMyProfileInputPort;
import co.dazma.api.users.services.usecases.contract.GetMyProfileUseCase;
import org.springframework.stereotype.Service;

@Service
public class GetMyProfileInputAdapter implements GetMyProfileInputPort {

    private final GetMyProfileUseCase useCase;
    private final MyProfileMapper mapper;

    public GetMyProfileInputAdapter(GetMyProfileUseCase useCase, MyProfileMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @Override
    public GetMyProfileOutputDto execute(GetMyProfileInputDto input) {
        User user = useCase.execute(input.externalId());
        return mapper.toOutput(user);
    }
}
