package co.dazma.api.security.infrastructure.output.firebase;

import co.dazma.api.security.domain.InvalidIdentityTokenException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.SessionCookieOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FirebaseSessionTokenIssuerTest {

    private FirebaseAuth firebaseAuth;
    private FirebaseSessionTokenIssuer issuer;

    @BeforeEach
    void setUp() {
        firebaseAuth = mock(FirebaseAuth.class);
        issuer = new FirebaseSessionTokenIssuer(firebaseAuth);
    }

    @Test
    void shouldCreateASessionCookieFromTheIdToken() throws FirebaseAuthException {
        when(firebaseAuth.createSessionCookie(eq("id-token"), any(SessionCookieOptions.class)))
                .thenReturn("session-token");

        String sessionToken = issuer.issue("id-token", Duration.ofHours(8));

        assertEquals("session-token", sessionToken);
    }

    @Test
    void shouldRejectAnIdTokenThatFirebaseDoesNotAccept() throws FirebaseAuthException {
        FirebaseAuthException firebaseException = mock(FirebaseAuthException.class);
        when(firebaseAuth.createSessionCookie(eq("invalid-token"), any(SessionCookieOptions.class)))
                .thenThrow(firebaseException);

        InvalidIdentityTokenException exception = assertThrows(
                InvalidIdentityTokenException.class,
                () -> issuer.issue("invalid-token", Duration.ofHours(8))
        );

        assertSame(firebaseException, exception.getCause());
    }

    @Test
    void shouldRevokeTheRefreshTokensOfTheUser() throws FirebaseAuthException {
        issuer.revokeSessions("firebase-user-123");

        verify(firebaseAuth).revokeRefreshTokens("firebase-user-123");
    }

    @Test
    void shouldReportARevocationFailure() throws FirebaseAuthException {
        FirebaseAuthException firebaseException = mock(FirebaseAuthException.class);
        doThrow(firebaseException).when(firebaseAuth).revokeRefreshTokens("firebase-user-123");

        assertThrows(InvalidIdentityTokenException.class, () -> issuer.revokeSessions("firebase-user-123"));
    }
}
