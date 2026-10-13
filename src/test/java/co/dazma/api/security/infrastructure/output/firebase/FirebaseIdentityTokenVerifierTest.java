package co.dazma.api.security.infrastructure.output.firebase;

import co.dazma.api.security.domain.InvalidIdentityTokenException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class FirebaseIdentityTokenVerifierTest {

    private FirebaseAuth firebaseAuth;
    private FirebaseIdentityTokenVerifier verifier;

    @BeforeEach
    void setUp() {
        firebaseAuth = mock(FirebaseAuth.class);
        verifier = new FirebaseIdentityTokenVerifier(firebaseAuth);
    }

    @Test
    void shouldReturnUidWhenTokenIsValid() throws FirebaseAuthException {
        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getUid()).thenReturn("firebase-user-123");
        when(firebaseAuth.verifyIdToken("valid-token")).thenReturn(firebaseToken);

        String uid = verifier.verify("valid-token");

        assertEquals("firebase-user-123", uid);
    }

    @Test
    void shouldRejectBlankTokenWithoutCallingFirebase() {
        assertThrows(InvalidIdentityTokenException.class, () -> verifier.verify(" "));

        verifyNoInteractions(firebaseAuth);
    }

    @Test
    void shouldRejectTokenWhenFirebaseCannotVerifyIt() throws FirebaseAuthException {
        FirebaseAuthException firebaseException = mock(FirebaseAuthException.class);
        when(firebaseAuth.verifyIdToken("invalid-token")).thenThrow(firebaseException);

        InvalidIdentityTokenException exception = assertThrows(
                InvalidIdentityTokenException.class,
                () -> verifier.verify("invalid-token")
        );

        assertSame(firebaseException, exception.getCause());
    }
}
