package co.dazma.api.security.services.usecases;

import co.dazma.api.security.domain.RecentSignInRequiredException;
import co.dazma.api.security.services.input.dto.OpenSessionInputDto;
import co.dazma.api.security.services.input.dto.OpenSessionOutputDto;
import co.dazma.api.security.services.input.ports.OpenSessionUseCase;
import co.dazma.api.security.services.output.ports.SessionTokenIssuer;

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
    public OpenSessionOutputDto open(OpenSessionInputDto input) {
        requireRecentSignIn(sessionTokenIssuer.signInTimeOf(input.idToken()));

        String sessionToken = sessionTokenIssuer.issue(input.idToken(), sessionValidity);
        return new OpenSessionOutputDto(sessionToken, sessionValidity);
    }

    private void requireRecentSignIn(Instant signInTime) {
        if (signInTime.plus(maximumSignInAge).isBefore(clock.instant())) {
            throw new RecentSignInRequiredException();
        }
    }
}
