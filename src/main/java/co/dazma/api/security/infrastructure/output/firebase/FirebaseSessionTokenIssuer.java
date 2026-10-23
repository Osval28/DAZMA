package co.dazma.api.security.infrastructure.output.firebase;

import co.dazma.api.security.domain.InvalidIdentityTokenException;
import co.dazma.api.security.services.output.ports.SessionTokenIssuer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.SessionCookieOptions;

import java.time.Duration;

public class FirebaseSessionTokenIssuer implements SessionTokenIssuer {

    private final FirebaseAuth firebaseAuth;

    public FirebaseSessionTokenIssuer(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public String issue(String idToken, Duration validity) {
        SessionCookieOptions options = SessionCookieOptions.builder()
                .setExpiresIn(validity.toMillis())
                .build();
        try {
            return firebaseAuth.createSessionCookie(idToken, options);
        } catch (FirebaseAuthException exception) {
            throw new InvalidIdentityTokenException(exception);
        }
    }

    @Override
    public void revokeSessions(String uid) {
        try {
            firebaseAuth.revokeRefreshTokens(uid);
        } catch (FirebaseAuthException exception) {
            throw new InvalidIdentityTokenException(exception);
        }
    }
}
