package co.dazma.api.users.services.usecases.impl;

import co.dazma.api.shared.domain.Municipality;
import co.dazma.api.users.domain.Email;
import co.dazma.api.users.domain.Name;
import co.dazma.api.users.domain.Phone;
import co.dazma.api.users.domain.State;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.domain.UserNotFoundException;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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
    void shouldReturnTheUserOfTheAuthenticatedIdentity() {
        User user = registeredUser();
        when(userRepository.findByExternalId("firebase-123")).thenReturn(Optional.of(user));

        User profile = service.execute("firebase-123");

        assertSame(user, profile);
    }

    @Test
    void shouldReturnASuspendedUser() {
        User user = registeredUser();
        user.suspend();
        when(userRepository.findByExternalId("firebase-123")).thenReturn(Optional.of(user));

        User profile = service.execute("firebase-123");

        assertEquals(State.SUSPENDED, profile.getState());
    }

    @Test
    void shouldRejectAnIdentityWithoutARegisteredProfile() {
        when(userRepository.findByExternalId("firebase-unknown")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> service.execute("firebase-unknown")
        );

        assertEquals("USER_NOT_FOUND", exception.code());
    }

    private static User registeredUser() {
        return User.register(new Name("Ana"), new Phone("3001234567"), new Municipality("Rionegro"),
                "firebase-123", new Email("ana@dazma.store"));
    }
}
