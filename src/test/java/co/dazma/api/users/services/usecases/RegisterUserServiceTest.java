package co.dazma.api.users.services.usecases;

import co.dazma.api.shared.domain.Municipality;
import co.dazma.api.users.domain.Email;
import co.dazma.api.users.domain.Phone;
import co.dazma.api.users.domain.Name;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.domain.UserAlreadyRegisteredException;
import co.dazma.api.users.events.UserRegistered;
import co.dazma.api.users.services.output.ports.UserEventsPort;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionOperations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegisterUserServiceTest {

    private UserRepositoryPort userRepository;
    private UserEventsPort userEvents;
    private RegisterUserService registerUserService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepositoryPort.class);
        userEvents = mock(UserEventsPort.class);
        registerUserService = new RegisterUserService(
                userRepository,
                userEvents,
                TransactionOperations.withoutTransaction()
        );
    }

    @Test
    void shouldRegisterANewUser() {
        when(userRepository.findByExternalId("firebase-123"))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = registerUserService.registerUser(
                "Ana",
                "3001234567",
                "Rionegro",
                "firebase-123",
                "ana@dazma.store"
        );

        assertNotNull(registeredUser.getId());
        assertEquals(new Name("Ana"), registeredUser.getName());
        assertEquals("firebase-123", registeredUser.getExternalId());

        verify(userRepository).save(registeredUser);
    }

    @Test
    void shouldAnnounceTheRegisteredUserWithItsEmail() {
        when(userRepository.findByExternalId("firebase-123"))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = registerUserService.registerUser(
                "Ana",
                "3001234567",
                "Rionegro",
                "firebase-123",
                "ana@dazma.store"
        );

        verify(userEvents).publish(new UserRegistered(registeredUser.getId(), "ana@dazma.store"));
    }

    @Test
    void shouldRejectAnAlreadyRegisteredUser() {
        User existingUser = User.register(new Name("Ana"),
                new Phone("3001234567"),
                new Municipality("Rionegro"),
                "firebase-123",
                new Email("ana@dazma.store")
        );

        when(userRepository.findByExternalId("firebase-123"))
                .thenReturn(Optional.of(existingUser));

        UserAlreadyRegisteredException exception = assertThrows(
                UserAlreadyRegisteredException.class,
                () -> registerUserService.registerUser(
                        "Ana",
                        "3001234567",
                        "Rionegro",
                        "firebase-123",
                        "ana@dazma.store"
                )
        );

        assertEquals("USER_ALREADY_REGISTERED", exception.code());
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(userEvents);
    }
}
