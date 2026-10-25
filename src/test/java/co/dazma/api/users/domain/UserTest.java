package co.dazma.api.users.domain;

import co.dazma.api.shared.domain.Municipality;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void registeredUserShouldBeActiveAndHaveUserRole() {

        User user = User.register(new Name("Ana"), new Phone("3001234567"), new Municipality("Rionegro"), "uid-123", new Email("ana@dazma.store"));

        assertEquals(State.ACTIVE, user.getState());
        assertEquals(Role.USER, user.getRole());
        assertNotNull(user.getId());
    }

    @Test
    void cannotRegisterUserWithEmptyName() {
        InvalidUserException exception = assertThrows(
                InvalidUserException.class,
                () -> User.register(new Name(""), new Phone("3001234567"), new Municipality("Rionegro"), "uid-123", new Email("ana@dazma.store"))
        );

        assertEquals("USER_NAME_REQUIRED", exception.code());
    }

    @Test
    void cannotSuspendAlreadySuspendedUser() {
        User user = User.register(new Name("Ana"), new Phone("3001234567"), new Municipality("Rionegro"), "uid-123", new Email("ana@dazma.store"));
        user.suspend();

        UserAlreadySuspendedException exception = assertThrows(
                UserAlreadySuspendedException.class,
                user::suspend
        );

        assertEquals("USER_ALREADY_SUSPENDED", exception.code());
    }

    @Test
    void cannotReactivateActiveUser() {
        User user = User.register(new Name("Ana"), new Phone("3001234567"), new Municipality("Rionegro"), "uid-123", new Email("ana@dazma.store"));

        UserNotSuspendedException exception = assertThrows(
                UserNotSuspendedException.class,
                user::reactivate
        );

        assertEquals("USER_NOT_SUSPENDED", exception.code());
    }

    @Test
    void canOperateReturnsTrueForActiveUser() {
        User user = User.register(new Name("Ana"), new Phone("3001234567"), new Municipality("Rionegro"), "uid-123", new Email("ana@dazma.store"));
        assertTrue(user.canOperate());
    }

    @Test
    void registeredUserKeepsTheProvidedData() {
        User user = User.register(new Name("Ana"), new Phone("3001234567"), new Municipality("Rionegro"), "uid-123", new Email("ana@dazma.store"));

        assertEquals(new Name("Ana"), user.getName());
        assertEquals(new Phone("3001234567"), user.getPhone());
        assertEquals("Rionegro", user.getMunicipality().name());
        assertEquals("uid-123", user.getExternalId());
    }

    @Test
    void canOperateReturnsFalseForInactiveUser() {
        User user = User.register(new Name("Ana"), new Phone("3001234567"), new Municipality("Rionegro"), "uid-123", new Email("ana@dazma.store"));
        user.suspend();

        assertFalse(user.canOperate());
    }

    @Test
    void suspendedUserCanBeReactivated() {
        User user = User.register(new Name("Ana"), new Phone("3001234567"), new Municipality("Rionegro"), "uid-123", new Email("ana@dazma.store"));
        user.suspend();

        user.reactivate();

        assertEquals(State.ACTIVE, user.getState());
        assertTrue(user.canOperate());
    }
}
