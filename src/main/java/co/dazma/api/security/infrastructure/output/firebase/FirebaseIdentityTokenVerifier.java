package co.dazma.api.security.infrastructure.output.firebase;

import co.dazma.api.security.domain.InvalidIdentityTokenException;
import co.dazma.api.security.services.output.ports.IdentityTokenVerifier;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class FirebaseIdentityTokenVerifier implements IdentityTokenVerifier {

    private final FirebaseAuth firebaseAuth;

    public FirebaseIdentityTokenVerifier(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public String verify(String sessionToken, boolean checkRevoked) {
        if (sessionToken == null || sessionToken.isBlank()) {
            throw new InvalidIdentityTokenException();
        }

        try {
            return firebaseAuth.verifySessionCookie(sessionToken, checkRevoked).getUid();
        } catch (FirebaseAuthException exception) {
            throw new InvalidIdentityTokenException(exception);
        }
    }
}
