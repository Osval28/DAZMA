package co.dazma.api.security.services.input.adapters;

import co.dazma.api.security.domain.Session;
import co.dazma.api.security.services.input.adapters.mappers.SessionMapper;
import co.dazma.api.security.services.input.dto.OpenSessionInputDto;
import co.dazma.api.security.services.input.dto.OpenSessionOutputDto;
import co.dazma.api.security.services.input.ports.OpenSessionInputPort;
import co.dazma.api.security.services.usecases.contract.OpenSessionUseCase;

public class OpenSessionInputAdapter implements OpenSessionInputPort {

    private final OpenSessionUseCase useCase;
    private final SessionMapper mapper;

    public OpenSessionInputAdapter(OpenSessionUseCase useCase, SessionMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @Override
    public OpenSessionOutputDto execute(OpenSessionInputDto input) {
        Session session = useCase.execute(input.idToken());
        return mapper.toOutput(session);
    }
}
