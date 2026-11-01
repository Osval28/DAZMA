package co.dazma.api.users.services.usecases.impl;

import co.dazma.api.shared.domain.Municipality;
import co.dazma.api.shared.domain.errors.InvalidMunicipalityException;
import co.dazma.api.shared.services.output.ports.MunicipalityCatalogPort;
import co.dazma.api.users.domain.Email;
import co.dazma.api.users.domain.Name;
import co.dazma.api.users.domain.Phone;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.domain.UserAlreadyRegisteredException;
import co.dazma.api.users.domain.UserRegistration;
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
    private MunicipalityCatalogPort municipalityCatalog;
    private UserEventsPort userEvents;
    private RegisterUserService registerUserService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepositoryPort.class);
        municipalityCatalog = mock(MunicipalityCatalogPort.class);
        when(municipalityCatalog.exists(new Municipality("05615"))).thenReturn(true);
        userEvents = mock(UserEventsPort.class);
        registerUserService = new RegisterUserService(
                userRepository,
                municipalityCatalog,
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

        User registeredUser = registerUserService.execute(anaRegistration());

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

        User registeredUser = registerUserService.execute(anaRegistration());

        verify(userEvents).publish(new UserRegistered(
                registeredUser.getId(),
                "firebase-123",
                "Ana",
                "ana@dazma.store",
                "ACTIVE"
        ));
    }

    @Test
    void shouldRejectAnAlreadyRegisteredUser() {
        User existingUser = User.register(new Name("Ana"),
                new Phone("3001234567"),
                new Municipality("05615"),
                "firebase-123",
                new Email("ana@dazma.store")
        );

        when(userRepository.findByExternalId("firebase-123"))
                .thenReturn(Optional.of(existingUser));

        UserAlreadyRegisteredException exception = assertThrows(
                UserAlreadyRegisteredException.class,
                () -> registerUserService.execute(anaRegistration())
        );

        assertEquals("USER_ALREADY_REGISTERED", exception.code());
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(userEvents);
    }

    @Test
    void shouldRejectAMunicipalityThatIsNotInTheCatalog() {
        when(municipalityCatalog.exists(new Municipality("05615"))).thenReturn(false);

        InvalidMunicipalityException exception = assertThrows(
                InvalidMunicipalityException.class,
                () -> registerUserService.execute(anaRegistration())
        );

        assertEquals("MUNICIPALITY_NOT_FOUND", exception.code());
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(userEvents);
    }

    private static UserRegistration anaRegistration() {
        return new UserRegistration(
                new Name("Ana"),
                new Phone("3001234567"),
                new Municipality("05615"),
                "firebase-123",
                new Email("ana@dazma.store")
        );
    }
}
