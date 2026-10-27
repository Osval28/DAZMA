package co.dazma.api.security.services.input.ports;

import co.dazma.api.security.services.input.dto.OpenSessionInputDto;
import co.dazma.api.security.services.input.dto.OpenSessionOutputDto;
import co.dazma.api.shared.services.input.ports.InputPortWithReturn;

public interface OpenSessionInputPort extends InputPortWithReturn<OpenSessionInputDto, OpenSessionOutputDto> {
}
