package co.dazma.api.security.services.input.ports;

import co.dazma.api.security.services.input.dto.AuthenticateIdentityInputDto;
import co.dazma.api.security.services.input.dto.AuthenticateIdentityOutputDto;
import co.dazma.api.shared.services.input.ports.InputPortWithReturn;

public interface AuthenticateIdentityInputPort
        extends InputPortWithReturn<AuthenticateIdentityInputDto, AuthenticateIdentityOutputDto> {
}
