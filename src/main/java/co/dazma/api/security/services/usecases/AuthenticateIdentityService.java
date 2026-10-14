package co.dazma.api.security.services.usecases;

import co.dazma.api.security.services.input.ports.AuthenticateIdentityUseCase;
import co.dazma.api.security.services.output.ports.IdentityTokenVerifier;

public class AuthenticateIdentityService implements AuthenticateIdentityUseCase {

    private final IdentityTokenVerifier identityTokenVerifier;

    public AuthenticateIdentityService(IdentityTokenVerifier identityTokenVerifier) {
        this.identityTokenVerifier = identityTokenVerifier;
    }

    @Override
    public String authenticate(String token) {
        return identityTokenVerifier.verify(token);
    }
}
