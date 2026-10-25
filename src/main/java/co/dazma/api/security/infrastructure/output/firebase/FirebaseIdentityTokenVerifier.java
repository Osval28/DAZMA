package co.dazma.api.security.infrastructure.output.firebase;

import co.dazma.api.security.domain.AuthenticatedIdentity;
import co.dazma.api.security.domain.InvalidIdentityTokenException;
import co.dazma.api.security.services.output.ports.IdentityTokenVerifier;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

public class FirebaseIdentityTokenVerifier implements IdentityTokenVerifier {

    private final FirebaseAuth firebaseAuth;

    public FirebaseIdentityTokenVerifier(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public AuthenticatedIdentity verify(String sessionToken, boolean checkRevoked) {
        if (sessionToken == null || sessionToken.isBlank()) {
            throw new InvalidIdentityTokenException();
        }

        try {
            FirebaseToken token = firebaseAuth.verifySessionCookie(sessionToken, checkRevoked);
            if (token.getEmail() == null || token.getEmail().isBlank()) {
                throw new InvalidIdentityTokenException();
            }
            return new AuthenticatedIdentity(token.getUid(), token.getEmail(), token.isEmailVerified());
        } catch (FirebaseAuthException exception) {
            throw new InvalidIdentityTokenException(exception);
        }
    }
}
