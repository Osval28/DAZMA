package co.dazma.api.security.services.usecases;

import co.dazma.api.security.domain.InvalidIdentityTokenException;
import co.dazma.api.security.services.output.ports.IdentityTokenVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticateIdentityServiceTest {

    private IdentityTokenVerifier identityTokenVerifier;
    private AuthenticateIdentityService service;

    @BeforeEach
    void setUp() {
        identityTokenVerifier = mock(IdentityTokenVerifier.class);
        service = new AuthenticateIdentityService(identityTokenVerifier);
    }

    @Test
    void shouldReturnUidProvidedByIdentityVerifier() {
        when(identityTokenVerifier.verify("valid-token"))
                .thenReturn("firebase-user-123");

        String uid = service.authenticate("valid-token");

        assertEquals("firebase-user-123", uid);
    }

    @Test
    void shouldPropagateInvalidIdentityToken() {
        when(identityTokenVerifier.verify("invalid-token"))
                .thenThrow(new InvalidIdentityTokenException());

        assertThrows(
                InvalidIdentityTokenException.class,
                () -> service.authenticate("invalid-token")
        );
    }
}
