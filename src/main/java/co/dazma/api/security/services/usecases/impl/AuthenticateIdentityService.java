package co.dazma.api.security.services.usecases.impl;

import co.dazma.api.security.domain.AuthenticatedIdentity;
import co.dazma.api.security.domain.TokenVerification;
import co.dazma.api.security.services.output.ports.IdentityTokenVerifier;
import co.dazma.api.security.services.usecases.contract.AuthenticateIdentityUseCase;

public class AuthenticateIdentityService implements AuthenticateIdentityUseCase {

    private final IdentityTokenVerifier identityTokenVerifier;

    public AuthenticateIdentityService(IdentityTokenVerifier identityTokenVerifier) {
        this.identityTokenVerifier = identityTokenVerifier;
    }

    @Override
    public AuthenticatedIdentity execute(TokenVerification verification) {
        return identityTokenVerifier.verify(verification.token(), verification.checkRevoked());
    }
}
