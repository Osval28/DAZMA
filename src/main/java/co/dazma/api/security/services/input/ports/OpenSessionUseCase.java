package co.dazma.api.security.services.input.ports;

import co.dazma.api.security.services.input.dto.OpenSessionInputDto;
import co.dazma.api.security.services.input.dto.OpenSessionOutputDto;

public interface OpenSessionUseCase {

    OpenSessionOutputDto open(OpenSessionInputDto input);
}
