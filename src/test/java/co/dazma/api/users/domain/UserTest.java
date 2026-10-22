package co.dazma.api.users.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void registeredUserShouldBeActiveAndHaveUserRole() {

        User user = User.register(new Name("Ana"), new Phone("3001234567"), "Rionegro", "uid-123");

        assertEquals(State.ACTIVE, user.getState());
        assertEquals(Role.USER, user.getRole());
        assertNotNull(user.getId());
    }

    @Test
    void cannotRegisterUserWithEmptyName() {
        InvalidUserException exception = assertThrows(
                InvalidUserException.class,
                () -> User.register(new Name(""), new Phone("3001234567"), "Rionegro", "uid-123")
        );

        assertEquals("USER_NAME_REQUIRED", exception.code());
    }

    @Test
    void cannotRegisterUserWithBlankMunicipality() {
        MissingRequiredFieldException exception = assertThrows(
                MissingRequiredFieldException.class,
                () -> User.register(new Name("Ana"), new Phone("3001234567"), "   ", "uid-123")
        );

        assertEquals("USER_REQUIRED_FIELD_MISSING", exception.code());
        assertArrayEquals(new Object[]{"municipality"}, exception.arguments());
    }

    @Test
    void cannotSuspendAlreadySuspendedUser() {
        User user = User.register(new Name("Ana"), new Phone("3001234567"), "Rionegro", "uid-123");
        user.suspend();

        UserAlreadySuspendedException exception = assertThrows(
                UserAlreadySuspendedException.class,
                user::suspend
        );

        assertEquals("USER_ALREADY_SUSPENDED", exception.code());
    }

    @Test
    void cannotReactivateActiveUser() {
        User user = User.register(new Name("Ana"), new Phone("3001234567"), "Rionegro", "uid-123");

        UserNotSuspendedException exception = assertThrows(
                UserNotSuspendedException.class,
                user::reactivate
        );

        assertEquals("USER_NOT_SUSPENDED", exception.code());
    }

    @Test
    void canOperateReturnsTrueForActiveUser() {
        User user = User.register(new Name("Ana"), new Phone("3001234567"), "Rionegro", "uid-123");
        assertTrue(user.canOperate());
    }

    @Test
    void registeredUserKeepsTheProvidedData() {
        User user = User.register(new Name("Ana"), new Phone("3001234567"), "Rionegro", "uid-123");

        assertEquals(new Name("Ana"), user.getName());
        assertEquals(new Phone("3001234567"), user.getPhone());
        assertEquals("Rionegro", user.getMunicipality());
        assertEquals("uid-123", user.getExternalId());
    }

    @Test
    void canOperateReturnsFalseForInactiveUser() {
        User user = User.register(new Name("Ana"), new Phone("3001234567"), "Rionegro", "uid-123");
        user.suspend();

        assertFalse(user.canOperate());
    }

    @Test
    void suspendedUserCanBeReactivated() {
        User user = User.register(new Name("Ana"), new Phone("3001234567"), "Rionegro", "uid-123");
        user.suspend();

        user.reactivate();

        assertEquals(State.ACTIVE, user.getState());
        assertTrue(user.canOperate());
    }
}
