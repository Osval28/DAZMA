package co.dazma.api.users.services.input.adapters;

import co.dazma.api.users.domain.User;
import co.dazma.api.users.services.input.adapters.mappers.RegisterUserMapper;
import co.dazma.api.users.services.input.dto.RegisterUserInputDto;
import co.dazma.api.users.services.input.dto.RegisterUserOutputDto;
import co.dazma.api.users.services.input.ports.RegisterUserInputPort;
import co.dazma.api.users.services.usecases.contract.RegisterUserUseCase;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserInputAdapter implements RegisterUserInputPort {

    private final RegisterUserUseCase useCase;
    private final RegisterUserMapper mapper;

    public RegisterUserInputAdapter(RegisterUserUseCase useCase, RegisterUserMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @Override
    public RegisterUserOutputDto execute(RegisterUserInputDto input) {
        User registeredUser = useCase.execute(mapper.toDomain(input));
        return mapper.toOutput(registeredUser);
    }
}
