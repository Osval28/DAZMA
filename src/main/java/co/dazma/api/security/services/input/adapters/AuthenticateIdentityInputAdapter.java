package co.dazma.api.security.services.input.adapters;

import co.dazma.api.security.domain.AuthenticatedIdentity;
import co.dazma.api.security.services.input.adapters.mappers.AuthenticatedIdentityMapper;
import co.dazma.api.security.services.input.dto.AuthenticateIdentityInputDto;
import co.dazma.api.security.services.input.dto.AuthenticateIdentityOutputDto;
import co.dazma.api.security.services.input.ports.AuthenticateIdentityInputPort;
import co.dazma.api.security.services.usecases.contract.AuthenticateIdentityUseCase;

public class AuthenticateIdentityInputAdapter implements AuthenticateIdentityInputPort {

    private final AuthenticateIdentityUseCase useCase;
    private final AuthenticatedIdentityMapper mapper;

    public AuthenticateIdentityInputAdapter(AuthenticateIdentityUseCase useCase, AuthenticatedIdentityMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @Override
    public AuthenticateIdentityOutputDto execute(AuthenticateIdentityInputDto input) {
        AuthenticatedIdentity identity = useCase.execute(mapper.toDomain(input));
        return mapper.toOutput(identity);
    }
}
