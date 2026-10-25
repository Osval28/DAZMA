package co.dazma.api.security.infrastructure.output.firebase;

import co.dazma.api.security.domain.AuthenticatedIdentity;
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
    void shouldReturnTheIdentityWhenTokenIsValid() throws FirebaseAuthException {
        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getUid()).thenReturn("firebase-user-123");
        when(firebaseToken.getEmail()).thenReturn("ana@dazma.store");
        when(firebaseToken.isEmailVerified()).thenReturn(true);
        when(firebaseAuth.verifySessionCookie("valid-token", true)).thenReturn(firebaseToken);

        AuthenticatedIdentity identity = verifier.verify("valid-token", true);

        assertEquals(new AuthenticatedIdentity("firebase-user-123", "ana@dazma.store", true), identity);
    }

    @Test
    void shouldRejectBlankTokenWithoutCallingFirebase() {
        assertThrows(InvalidIdentityTokenException.class, () -> verifier.verify(" ", true));

        verifyNoInteractions(firebaseAuth);
    }

    @Test
    void shouldRejectTokenWhenFirebaseCannotVerifyIt() throws FirebaseAuthException {
        FirebaseAuthException firebaseException = mock(FirebaseAuthException.class);
        when(firebaseAuth.verifySessionCookie("invalid-token", true)).thenThrow(firebaseException);

        InvalidIdentityTokenException exception = assertThrows(
                InvalidIdentityTokenException.class,
                () -> verifier.verify("invalid-token", true)
        );

        assertSame(firebaseException, exception.getCause());
    }

    @Test
    void shouldSkipTheRevocationCheckWhenNotRequired() throws FirebaseAuthException {
        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getUid()).thenReturn("firebase-user-123");
        when(firebaseToken.getEmail()).thenReturn("ana@dazma.store");
        when(firebaseAuth.verifySessionCookie("valid-token", false)).thenReturn(firebaseToken);

        assertEquals("firebase-user-123", verifier.verify("valid-token", false).uid());
    }

    @Test
    void shouldRejectATokenWithoutEmail() throws FirebaseAuthException {
        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getUid()).thenReturn("firebase-user-123");
        when(firebaseAuth.verifySessionCookie("token-without-email", true)).thenReturn(firebaseToken);

        assertThrows(
                InvalidIdentityTokenException.class,
                () -> verifier.verify("token-without-email", true)
        );
    }
}
