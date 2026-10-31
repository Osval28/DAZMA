package co.dazma.api.users.services.input.adapters;

import co.dazma.api.shared.domain.Municipality;
import co.dazma.api.users.domain.Email;
import co.dazma.api.users.domain.Name;
import co.dazma.api.users.domain.Phone;
import co.dazma.api.users.domain.Role;
import co.dazma.api.users.domain.State;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.services.input.adapters.mappers.MyProfileMapper;
import co.dazma.api.users.services.input.dto.GetMyProfileInputDto;
import co.dazma.api.users.services.input.dto.GetMyProfileOutputDto;
import co.dazma.api.users.services.usecases.contract.GetMyProfileUseCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetMyProfileInputAdapterTest {

    @Test
    void shouldReturnTheProfileOfTheAuthenticatedIdentity() {
        GetMyProfileUseCase useCase = mock(GetMyProfileUseCase.class);
        GetMyProfileInputAdapter adapter = new GetMyProfileInputAdapter(useCase, new MyProfileMapper());
        User user = User.register(new Name("Ana"), new Phone("3001234567"), new Municipality("05615"),
                "firebase-123", new Email("ana@dazma.store"));
        when(useCase.execute("firebase-123")).thenReturn(user);

        GetMyProfileOutputDto profile = adapter.execute(new GetMyProfileInputDto("firebase-123"));

        assertEquals(user.getId(), profile.id());
        assertEquals("Ana", profile.name());
        assertEquals("3001234567", profile.phone());
        assertEquals("05615", profile.municipality());
        assertEquals("ana@dazma.store", profile.email());
        assertEquals(State.ACTIVE, profile.state());
        assertEquals(Role.USER, profile.role());
    }
}
