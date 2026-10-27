package co.dazma.api.users.services.input.ports;

import co.dazma.api.shared.services.input.ports.InputPortWithReturn;
import co.dazma.api.users.services.input.dto.GetMyProfileInputDto;
import co.dazma.api.users.services.input.dto.GetMyProfileOutputDto;

public interface GetMyProfileInputPort extends InputPortWithReturn<GetMyProfileInputDto, GetMyProfileOutputDto> {
}
