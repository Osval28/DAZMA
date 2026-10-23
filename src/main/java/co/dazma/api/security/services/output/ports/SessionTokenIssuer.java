package co.dazma.api.security.services.output.ports;

import java.time.Duration;
import java.time.Instant;

public interface SessionTokenIssuer {

    Instant signInTimeOf(String idToken);

    String issue(String idToken, Duration validity);

    void revokeSessions(String uid);
}
