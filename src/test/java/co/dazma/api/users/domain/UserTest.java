package co.dazma.api.users.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void registeredUserShouldBeActiveAndHaveUserRole() {
        // preparar y ejecutar
        User user = User.register("Ana", "3001234567", "Rionegro", "uid-123");

        // verificar
        assertEquals(State.ACTIVE, user.getState());
        assertEquals(Role.USER, user.getRole());
        assertNotNull(user.getId());
    }

    @Test
    void cannotRegisterUserWithEmptyName() {
        assertThrows(NotEnoughArgumentsException.class,
                () -> User.register("", "3001234567", "Rionegro", "uid-123"));
    }

    @Test
    void cannotSuspendAlreadySuspendedUser() {
        User user = User.register("Ana", "3001234567", "Rionegro", "uid-123");
        user.suspend();

        assertThrows(IllegalStateException.class, () -> user.suspend());
    }

    @Test
    void cannotReactivateActiveUser() {
        User user = User.register("Ana", "3001234567", "Rionegro", "uid-123");

        assertThrows(IllegalStateException.class, () -> user.reactivate());
    }

    @Test
    void canOperateReturnsTrueForActiveUser() {
        User user = User.register("Ana", "3001234567", "Rionegro", "uid-123");
        assertTrue(user.canOperate());
    }

    @Test
    void registeredUserKeepsTheProvidedData() {
        User user = User.register("Ana", "3001234567", "Rionegro", "uid-123");

        assertEquals("Ana", user.getName());
        assertEquals("3001234567", user.getPhone());
        assertEquals("Rionegro", user.getMunicipality());
        assertEquals("uid-123", user.getExternalId());
    }


}