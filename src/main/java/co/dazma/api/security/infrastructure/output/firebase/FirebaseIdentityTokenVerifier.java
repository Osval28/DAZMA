package co.dazma.api.security.infrastructure.output.firebase;

import co.dazma.api.security.domain.InvalidIdentityTokenException;
import co.dazma.api.security.services.output.ports.IdentityTokenVerifier;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(FirebaseAuth.class)
public class FirebaseIdentityTokenVerifier implements IdentityTokenVerifier {

    private final FirebaseAuth firebaseAuth;

    public FirebaseIdentityTokenVerifier(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public String verify(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidIdentityTokenException();
        }

        try {
            return firebaseAuth.verifyIdToken(token).getUid();
        } catch (FirebaseAuthException exception) {
            throw new InvalidIdentityTokenException(exception);
        }
    }
}
