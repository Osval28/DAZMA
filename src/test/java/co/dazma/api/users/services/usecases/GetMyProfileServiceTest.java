package co.dazma.api.users.services.usecases;

import co.dazma.api.shared.domain.Municipality;
import co.dazma.api.users.domain.Email;
import co.dazma.api.users.domain.Name;
import co.dazma.api.users.domain.Phone;
import co.dazma.api.users.domain.Role;
import co.dazma.api.users.domain.State;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.domain.UserNotFoundException;
import co.dazma.api.users.services.input.dto.GetMyProfileInputDto;
import co.dazma.api.users.services.input.dto.GetMyProfileOutputDto;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetMyProfileServiceTest {

    private UserRepositoryPort userRepository;
    private GetMyProfileService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepositoryPort.class);
        service = new GetMyProfileService(userRepository);
    }

    @Test
    void shouldReturnTheProfileOfTheAuthenticatedIdentity() {
        User user = registeredUser();
        when(userRepository.findByExternalId("firebase-123")).thenReturn(Optional.of(user));

        GetMyProfileOutputDto profile = service.get(new GetMyProfileInputDto("firebase-123"));

        assertEquals(user.getId(), profile.id());
        assertEquals("Ana", profile.name());
        assertEquals("3001234567", profile.phone());
        assertEquals("Rionegro", profile.municipality());
        assertEquals("ana@dazma.store", profile.email());
        assertEquals(State.ACTIVE, profile.state());
        assertEquals(Role.USER, profile.role());
    }

    @Test
    void shouldReturnTheProfileOfASuspendedUser() {
        User user = registeredUser();
        user.suspend();
        when(userRepository.findByExternalId("firebase-123")).thenReturn(Optional.of(user));

        GetMyProfileOutputDto profile = service.get(new GetMyProfileInputDto("firebase-123"));

        assertEquals(State.SUSPENDED, profile.state());
    }

    @Test
    void shouldRejectAnIdentityWithoutARegisteredProfile() {
        when(userRepository.findByExternalId("firebase-unknown")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> service.get(new GetMyProfileInputDto("firebase-unknown"))
        );

        assertEquals("USER_NOT_FOUND", exception.code());
    }

    private static User registeredUser() {
        return User.register(new Name("Ana"), new Phone("3001234567"), new Municipality("Rionegro"),
                "firebase-123", new Email("ana@dazma.store"));
    }
}
