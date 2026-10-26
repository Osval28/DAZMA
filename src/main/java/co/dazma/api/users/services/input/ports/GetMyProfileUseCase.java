package co.dazma.api.users.services.input.ports;

import co.dazma.api.users.services.input.dto.GetMyProfileInputDto;
import co.dazma.api.users.services.input.dto.GetMyProfileOutputDto;

public interface GetMyProfileUseCase {
    GetMyProfileOutputDto get(GetMyProfileInputDto input);
}
