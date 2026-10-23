package co.dazma.api.security.services.input.ports;

import co.dazma.api.security.services.input.dto.CloseSessionInputDto;

public interface CloseSessionUseCase {

    void close(CloseSessionInputDto input);
}
