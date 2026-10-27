package co.dazma.api.security.services.usecases.impl;

import co.dazma.api.security.domain.RecentSignInRequiredException;
import co.dazma.api.security.domain.Session;
import co.dazma.api.security.services.output.ports.SessionTokenIssuer;
import co.dazma.api.security.services.usecases.contract.OpenSessionUseCase;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class OpenSessionService implements OpenSessionUseCase {

    private final SessionTokenIssuer sessionTokenIssuer;
    private final Duration sessionValidity;
    private final Duration maximumSignInAge;
    private final Clock clock;

    public OpenSessionService(
            SessionTokenIssuer sessionTokenIssuer,
            Duration sessionValidity,
            Duration maximumSignInAge,
            Clock clock
    ) {
        this.sessionTokenIssuer = sessionTokenIssuer;
        this.sessionValidity = sessionValidity;
        this.maximumSignInAge = maximumSignInAge;
        this.clock = clock;
    }

    @Override
    public Session execute(String idToken) {
        requireRecentSignIn(sessionTokenIssuer.signInTimeOf(idToken));

        String sessionToken = sessionTokenIssuer.issue(idToken, sessionValidity);
        return new Session(sessionToken, sessionValidity);
    }

    private void requireRecentSignIn(Instant signInTime) {
        if (signInTime.plus(maximumSignInAge).isBefore(clock.instant())) {
            throw new RecentSignInRequiredException();
        }
    }
}
