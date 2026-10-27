package co.dazma.api.security.services.input.adapters;

import co.dazma.api.security.domain.AuthenticatedIdentity;
import co.dazma.api.security.domain.TokenVerification;
import co.dazma.api.security.services.input.adapters.mappers.AuthenticatedIdentityMapper;
import co.dazma.api.security.services.input.dto.AuthenticateIdentityInputDto;
import co.dazma.api.security.services.input.dto.AuthenticateIdentityOutputDto;
import co.dazma.api.security.services.usecases.contract.AuthenticateIdentityUseCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticateIdentityInputAdapterTest {

    @Test
    void shouldTranslateTheTokenAndTheAuthenticatedIdentity() {
        AuthenticateIdentityUseCase useCase = mock(AuthenticateIdentityUseCase.class);
        AuthenticateIdentityInputAdapter adapter =
                new AuthenticateIdentityInputAdapter(useCase, new AuthenticatedIdentityMapper());
        when(useCase.execute(new TokenVerification("valid-token", true)))
                .thenReturn(new AuthenticatedIdentity("firebase-user-123", "ana@dazma.store", true));

        AuthenticateIdentityOutputDto identity =
                adapter.execute(new AuthenticateIdentityInputDto("valid-token", true));

        assertEquals(new AuthenticateIdentityOutputDto("firebase-user-123", "ana@dazma.store", true), identity);
    }
}
