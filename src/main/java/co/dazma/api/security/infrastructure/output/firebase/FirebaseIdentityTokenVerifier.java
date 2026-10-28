package co.dazma.api.security.infrastructure.output.firebase;

import co.dazma.api.security.domain.AuthenticatedIdentity;
import co.dazma.api.security.domain.InvalidIdentityTokenException;
import co.dazma.api.security.services.output.ports.IdentityTokenVerifier;
import co.dazma.api.shared.infrastructure.observability.ExternalDependencyObservation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

public class FirebaseIdentityTokenVerifier implements IdentityTokenVerifier {

    private final FirebaseAuth firebaseAuth;
    private final ExternalDependencyObservation observations;

    public FirebaseIdentityTokenVerifier(
            FirebaseAuth firebaseAuth,
            ExternalDependencyObservation observations
    ) {
        this.firebaseAuth = firebaseAuth;
        this.observations = observations;
    }

    @Override
    public AuthenticatedIdentity verify(String sessionToken, boolean checkRevoked) {
        if (sessionToken == null || sessionToken.isBlank()) {
            throw new InvalidIdentityTokenException();
        }

        try {
            FirebaseToken token = observations.observeChecked(
                    "firebase",
                    "verify_session",
                    () -> firebaseAuth.verifySessionCookie(sessionToken, checkRevoked)
            );
            if (token.getEmail() == null || token.getEmail().isBlank()) {
                throw new InvalidIdentityTokenException();
            }
            return new AuthenticatedIdentity(token.getUid(), token.getEmail(), token.isEmailVerified());
        } catch (FirebaseAuthException exception) {
            throw new InvalidIdentityTokenException(exception);
        }
    }
}
