package co.dazma.api.security.services.usecases.impl;

import co.dazma.api.security.domain.InvalidIdentityTokenException;
import co.dazma.api.security.services.output.ports.IdentityTokenVerifier;
import co.dazma.api.security.services.output.ports.SessionTokenIssuer;
import co.dazma.api.security.services.usecases.contract.CloseSessionUseCase;

import java.util.Optional;

public class CloseSessionService implements CloseSessionUseCase {

    private final IdentityTokenVerifier identityTokenVerifier;
    private final SessionTokenIssuer sessionTokenIssuer;

    public CloseSessionService(
            IdentityTokenVerifier identityTokenVerifier,
            SessionTokenIssuer sessionTokenIssuer
    ) {
        this.identityTokenVerifier = identityTokenVerifier;
        this.sessionTokenIssuer = sessionTokenIssuer;
    }

    @Override
    public void execute(String sessionToken) {
        uidOfStillValidSession(sessionToken).ifPresent(sessionTokenIssuer::revokeSessions);
    }

    private Optional<String> uidOfStillValidSession(String sessionToken) {
        try {
            return Optional.of(identityTokenVerifier.verify(sessionToken, false).uid());
        } catch (InvalidIdentityTokenException exception) {
            return Optional.empty();
        }
    }
}
