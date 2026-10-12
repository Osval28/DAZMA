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
}