package co.dazma.api.security.services.usecases;

import co.dazma.api.security.domain.AuthenticatedIdentity;
import co.dazma.api.security.domain.InvalidIdentityTokenException;
import co.dazma.api.security.services.output.ports.IdentityTokenVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticateIdentityServiceTest {

    private static final AuthenticatedIdentity IDENTITY =
            new AuthenticatedIdentity("firebase-user-123", "ana@dazma.store", true);

    private IdentityTokenVerifier identityTokenVerifier;
    private AuthenticateIdentityService service;

    @BeforeEach
    void setUp() {
        identityTokenVerifier = mock(IdentityTokenVerifier.class);
        service = new AuthenticateIdentityService(identityTokenVerifier);
    }

    @Test
    void shouldReturnTheIdentityProvidedByTheVerifier() {
        when(identityTokenVerifier.verify("valid-token", true))
                .thenReturn(IDENTITY);

        assertEquals(IDENTITY, service.authenticate("valid-token", true));
    }

    @Test
    void shouldPropagateInvalidIdentityToken() {
        when(identityTokenVerifier.verify("invalid-token", true))
                .thenThrow(new InvalidIdentityTokenException());

        assertThrows(
                InvalidIdentityTokenException.class,
                () -> service.authenticate("invalid-token", true)
        );
    }
}
