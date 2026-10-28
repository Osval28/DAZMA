package co.dazma.api.security.services.usecases.impl;

import co.dazma.api.security.domain.AuthenticatedIdentity;
import co.dazma.api.security.domain.InvalidIdentityTokenException;
import co.dazma.api.security.services.output.ports.IdentityTokenVerifier;
import co.dazma.api.security.services.output.ports.SessionTokenIssuer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CloseSessionServiceTest {

    private IdentityTokenVerifier identityTokenVerifier;
    private SessionTokenIssuer sessionTokenIssuer;
    private CloseSessionService service;

    @BeforeEach
    void setUp() {
        identityTokenVerifier = mock(IdentityTokenVerifier.class);
        sessionTokenIssuer = mock(SessionTokenIssuer.class);
        service = new CloseSessionService(identityTokenVerifier, sessionTokenIssuer);
    }

    @Test
    void shouldRevokeTheSessionsOfTheAuthenticatedUser() {
        when(identityTokenVerifier.verify("session-token", false)).thenReturn(
                new AuthenticatedIdentity("firebase-user-123", "ana@dazma.store", true)
        );

        service.execute("session-token");

        verify(sessionTokenIssuer).revokeSessions("firebase-user-123");
    }

    @Test
    void shouldCloseQuietlyWhenTheSessionIsNoLongerValid() {
        when(identityTokenVerifier.verify("expired-token", false))
                .thenThrow(new InvalidIdentityTokenException());

        service.execute("expired-token");

        verifyNoInteractions(sessionTokenIssuer);
    }
}
