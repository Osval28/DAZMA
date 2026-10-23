package co.dazma.api.security.services.usecases;

import co.dazma.api.security.domain.InvalidIdentityTokenException;
import co.dazma.api.security.services.input.dto.OpenSessionInputDto;
import co.dazma.api.security.services.input.dto.OpenSessionOutputDto;
import co.dazma.api.security.services.output.ports.SessionTokenIssuer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenSessionServiceTest {

    private static final Duration VALIDITY = Duration.ofHours(8);

    private SessionTokenIssuer sessionTokenIssuer;
    private OpenSessionService service;

    @BeforeEach
    void setUp() {
        sessionTokenIssuer = mock(SessionTokenIssuer.class);
        service = new OpenSessionService(sessionTokenIssuer, VALIDITY);
    }

    @Test
    void shouldExchangeTheIdTokenForASessionToken() {
        when(sessionTokenIssuer.issue("id-token", VALIDITY)).thenReturn("session-token");

        OpenSessionOutputDto session = service.open(new OpenSessionInputDto("id-token"));

        assertEquals("session-token", session.sessionToken());
        assertEquals(VALIDITY, session.validity());
    }

    @Test
    void shouldPropagateAnInvalidIdToken() {
        when(sessionTokenIssuer.issue("invalid-token", VALIDITY))
                .thenThrow(new InvalidIdentityTokenException());

        assertThrows(
                InvalidIdentityTokenException.class,
                () -> service.open(new OpenSessionInputDto("invalid-token"))
        );
    }
}
