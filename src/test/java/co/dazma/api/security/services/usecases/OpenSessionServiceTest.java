package co.dazma.api.security.services.usecases;

import co.dazma.api.security.domain.InvalidIdentityTokenException;
import co.dazma.api.security.domain.RecentSignInRequiredException;
import co.dazma.api.security.services.input.dto.OpenSessionInputDto;
import co.dazma.api.security.services.input.dto.OpenSessionOutputDto;
import co.dazma.api.security.services.output.ports.SessionTokenIssuer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OpenSessionServiceTest {

    private static final Duration VALIDITY = Duration.ofHours(8);
    private static final Duration MAXIMUM_SIGN_IN_AGE = Duration.ofMinutes(5);
    private static final Instant NOW = Instant.parse("2026-10-23T15:00:00Z");

    private SessionTokenIssuer sessionTokenIssuer;
    private OpenSessionService service;

    @BeforeEach
    void setUp() {
        sessionTokenIssuer = mock(SessionTokenIssuer.class);
        service = new OpenSessionService(
                sessionTokenIssuer,
                VALIDITY,
                MAXIMUM_SIGN_IN_AGE,
                Clock.fixed(NOW, ZoneOffset.UTC)
        );
    }

    @Test
    void shouldExchangeARecentIdTokenForASessionToken() {
        when(sessionTokenIssuer.signInTimeOf("id-token")).thenReturn(NOW.minus(Duration.ofMinutes(1)));
        when(sessionTokenIssuer.issue("id-token", VALIDITY)).thenReturn("session-token");

        OpenSessionOutputDto session = service.open(new OpenSessionInputDto("id-token"));

        assertEquals("session-token", session.sessionToken());
        assertEquals(VALIDITY, session.validity());
    }

    @Test
    void shouldAcceptASignInExactlyAtTheMaximumAge() {
        when(sessionTokenIssuer.signInTimeOf("id-token")).thenReturn(NOW.minus(MAXIMUM_SIGN_IN_AGE));
        when(sessionTokenIssuer.issue("id-token", VALIDITY)).thenReturn("session-token");

        assertEquals("session-token", service.open(new OpenSessionInputDto("id-token")).sessionToken());
    }

    @Test
    void shouldRejectAnIdTokenFromAnOldSignIn() {
        when(sessionTokenIssuer.signInTimeOf("old-token")).thenReturn(NOW.minus(Duration.ofMinutes(6)));

        assertThrows(
                RecentSignInRequiredException.class,
                () -> service.open(new OpenSessionInputDto("old-token"))
        );
        verify(sessionTokenIssuer, never()).issue(anyString(), any());
    }

    @Test
    void shouldPropagateAnInvalidIdToken() {
        when(sessionTokenIssuer.signInTimeOf("invalid-token"))
                .thenThrow(new InvalidIdentityTokenException());

        assertThrows(
                InvalidIdentityTokenException.class,
                () -> service.open(new OpenSessionInputDto("invalid-token"))
        );
    }
}
