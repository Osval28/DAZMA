package co.dazma.api.users.services.input.ports;

import co.dazma.api.shared.services.input.ports.InputPortWithReturn;
import co.dazma.api.users.services.input.dto.RegisterUserInputDto;
import co.dazma.api.users.services.input.dto.RegisterUserOutputDto;

public interface RegisterUserInputPort extends InputPortWithReturn<RegisterUserInputDto, RegisterUserOutputDto> {
}
