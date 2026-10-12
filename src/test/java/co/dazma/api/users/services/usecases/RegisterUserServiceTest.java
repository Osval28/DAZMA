package co.dazma.api.users.services.usecases;

import co.dazma.api.users.domain.User;
import co.dazma.api.users.domain.UserAlreadyRegisteredException;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegisterUserServiceTest {

    private UserRepositoryPort userRepository;
    private RegisterUserService registerUserService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepositoryPort.class);
        registerUserService = new RegisterUserService(userRepository);
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
                "firebase-123"
        );

        assertNotNull(registeredUser.getId());
        assertEquals("Ana", registeredUser.getName());
        assertEquals("firebase-123", registeredUser.getExternalId());

        verify(userRepository).save(registeredUser);
    }

    @Test
    void shouldRejectAnAlreadyRegisteredUser() {
        User existingUser = User.register(
                "Ana",
                "3001234567",
                "Rionegro",
                "firebase-123"
        );

        when(userRepository.findByExternalId("firebase-123"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(
                UserAlreadyRegisteredException.class,
                () -> registerUserService.registerUser(
                        "Ana",
                        "3001234567",
                        "Rionegro",
                        "firebase-123"
                )
        );

        verify(userRepository, never()).save(any(User.class));
    }
}