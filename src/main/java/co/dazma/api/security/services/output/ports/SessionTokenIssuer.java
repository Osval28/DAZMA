package co.dazma.api.security.services.output.ports;

import java.time.Duration;

public interface SessionTokenIssuer {

    String issue(String idToken, Duration validity);

    void revokeSessions(String uid);
}
