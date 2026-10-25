package co.dazma.api.security.services.output.ports;

import co.dazma.api.security.domain.AuthenticatedIdentity;

public interface IdentityTokenVerifier {

    AuthenticatedIdentity verify(String token, boolean checkRevoked);
}
