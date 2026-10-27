package co.dazma.api.users.services.input.adapters;

import co.dazma.api.shared.domain.Municipality;
import co.dazma.api.users.domain.Email;
import co.dazma.api.users.domain.InvalidUserException;
import co.dazma.api.users.domain.Name;
import co.dazma.api.users.domain.Phone;
import co.dazma.api.users.domain.Role;
import co.dazma.api.users.domain.State;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.domain.UserRegistration;
import co.dazma.api.users.services.input.adapters.mappers.RegisterUserMapper;
import co.dazma.api.users.services.input.dto.RegisterUserInputDto;
import co.dazma.api.users.services.input.dto.RegisterUserOutputDto;
import co.dazma.api.users.services.usecases.contract.RegisterUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RegisterUserInputAdapterTest {

    private RegisterUserUseCase useCase;
    private RegisterUserInputAdapter adapter;

    @BeforeEach
    void setUp() {
        useCase = mock(RegisterUserUseCase.class);
        adapter = new RegisterUserInputAdapter(useCase, new RegisterUserMapper());
    }

    @Test
    void shouldTranslateTheInputIntoDomainAndTheResultIntoOutput() {
        User user = User.register(new Name("Ana"), new Phone("3001234567"), new Municipality("Rionegro"),
                "firebase-123", new Email("ana@dazma.store"));
        when(useCase.execute(any(UserRegistration.class))).thenReturn(user);

        RegisterUserOutputDto output = adapter.execute(new RegisterUserInputDto(
                "Ana", "3001234567", "Rionegro", "firebase-123", "ana@dazma.store"
        ));

        ArgumentCaptor<UserRegistration> registration = ArgumentCaptor.forClass(UserRegistration.class);
        verify(useCase).execute(registration.capture());
        assertEquals(new Name("Ana"), registration.getValue().name());
        assertEquals(new Phone("3001234567"), registration.getValue().phone());
        assertEquals(new Municipality("Rionegro"), registration.getValue().municipality());
        assertEquals("firebase-123", registration.getValue().externalId());
        assertEquals(new Email("ana@dazma.store"), registration.getValue().email());

        assertEquals(user.getId(), output.id());
        assertEquals("Ana", output.name());
        assertEquals("3001234567", output.phone());
        assertEquals("Rionegro", output.municipality());
        assertEquals(State.ACTIVE, output.state());
        assertEquals(Role.USER, output.role());
    }

    @Test
    void shouldNotCallTheUseCaseWhenTheInputIsNotValidDomain() {
        assertThrows(InvalidUserException.class, () -> adapter.execute(new RegisterUserInputDto(
                " ", "3001234567", "Rionegro", "firebase-123", "ana@dazma.store"
        )));

        verifyNoInteractions(useCase);
    }
}
