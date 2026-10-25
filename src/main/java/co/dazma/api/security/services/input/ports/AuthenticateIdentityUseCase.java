package co.dazma.api.security.services.input.ports;

import co.dazma.api.security.domain.AuthenticatedIdentity;

public interface AuthenticateIdentityUseCase {

    AuthenticatedIdentity authenticate(String token, boolean checkRevoked);
}
