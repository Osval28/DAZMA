package co.dazma.api.security.infrastructure.output.firebase;

import co.dazma.api.security.domain.InvalidIdentityTokenException;
import co.dazma.api.security.domain.SessionRevocationException;
import co.dazma.api.security.services.output.ports.SessionTokenIssuer;
import co.dazma.api.shared.infrastructure.observability.ExternalDependencyObservation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.SessionCookieOptions;

import java.time.Duration;
import java.time.Instant;

public class FirebaseSessionTokenIssuer implements SessionTokenIssuer {

    private static final String AUTH_TIME_CLAIM = "auth_time";

    private final FirebaseAuth firebaseAuth;
    private final ExternalDependencyObservation observations;

    public FirebaseSessionTokenIssuer(
            FirebaseAuth firebaseAuth,
            ExternalDependencyObservation observations
    ) {
        this.firebaseAuth = firebaseAuth;
        this.observations = observations;
    }

    @Override
    public Instant signInTimeOf(String idToken) {
        try {
            Object authTime = observations.observeChecked(
                    "firebase",
                    "verify_id_token",
                    () -> firebaseAuth.verifyIdToken(idToken)
            ).getClaims().get(AUTH_TIME_CLAIM);
            if (!(authTime instanceof Number seconds)) {
                throw new InvalidIdentityTokenException();
            }
            return Instant.ofEpochSecond(seconds.longValue());
        } catch (FirebaseAuthException exception) {
            throw new InvalidIdentityTokenException(exception);
        }
    }

    @Override
    public String issue(String idToken, Duration validity) {
        SessionCookieOptions options = SessionCookieOptions.builder()
                .setExpiresIn(validity.toMillis())
                .build();
        try {
            return observations.observeChecked(
                    "firebase",
                    "issue_session",
                    () -> firebaseAuth.createSessionCookie(idToken, options)
            );
        } catch (FirebaseAuthException exception) {
            throw new InvalidIdentityTokenException(exception);
        }
    }

    @Override
    public void revokeSessions(String uid) {
        try {
            observations.observeChecked(
                    "firebase",
                    "revoke_sessions",
                    () -> firebaseAuth.revokeRefreshTokens(uid)
            );
        } catch (FirebaseAuthException exception) {
            throw new SessionRevocationException(exception);
        }
    }
}
