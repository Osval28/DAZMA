package co.dazma.api.users.services.usecases;

import co.dazma.api.shared.domain.Municipality;
import co.dazma.api.users.domain.Phone;
import co.dazma.api.users.domain.Name;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.domain.UserCannotOperateExceptionDomain;
import co.dazma.api.users.domain.UserNotFoundException;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResolveUserIdServiceTest {

    private UserRepositoryPort userRepository;
    private ResolveUserIdService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepositoryPort.class);
        service = new ResolveUserIdService(userRepository);
    }

    @Test
    void shouldResolveTheInternalIdFromTheExternalIdentity() {
        User user = User.register(new Name("Ana"), new Phone("3001234567"), new Municipality("Rionegro"), "firebase-123");
        when(userRepository.findByExternalId("firebase-123"))
                .thenReturn(Optional.of(user));

        UUID result = service.resolveByExternalId("firebase-123");

        assertEquals(user.getId(), result);
    }

    @Test
    void shouldRejectAnIdentityWithoutARegisteredProfile() {
        when(userRepository.findByExternalId("firebase-unknown"))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> service.resolveByExternalId("firebase-unknown")
        );

        assertEquals("USER_NOT_FOUND", exception.code());
    }

    @Test
    void shouldRejectASuspendedUser() {
        User user = User.register(new Name("Ana"), new Phone("3001234567"), new Municipality("Rionegro"), "firebase-123");
        user.suspend();
        when(userRepository.findByExternalId("firebase-123"))
                .thenReturn(Optional.of(user));

        UserCannotOperateExceptionDomain exception = assertThrows(
                UserCannotOperateExceptionDomain.class,
                () -> service.resolveByExternalId("firebase-123")
        );

        assertEquals("USER_CANNOT_OPERATE", exception.code());
    }
}
