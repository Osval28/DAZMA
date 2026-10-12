package co.dazma.api.users.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void registeredUserShouldBeActiveAndHaveUserRole() {

        User user = User.register("Ana", "3001234567", "Rionegro", "uid-123");

        assertEquals(State.ACTIVE, user.getState());
        assertEquals(Role.USER, user.getRole());
        assertNotNull(user.getId());
    }

    @Test
    void cannotRegisterUserWithEmptyName() {
        assertThrows(MissingRequiredFieldException.class,
                () -> User.register("", "3001234567", "Rionegro", "uid-123"));
    }

    @ParameterizedTest
    @MethodSource("missingRequiredFields")
    void cannotRegisterUserWhenARequiredFieldIsMissing(
            String name,
            String phone,
            String municipality,
            String externalId,
            String missingField
    ) {
        MissingRequiredFieldException exception = assertThrows(
                MissingRequiredFieldException.class,
                () -> User.register(name, phone, municipality, externalId)
        );

        assertEquals("Falta el campo: " + missingField, exception.getMessage());
    }

    @Test
    void cannotSuspendAlreadySuspendedUser() {
        User user = User.register("Ana", "3001234567", "Rionegro", "uid-123");
        user.suspend();

        assertThrows(IllegalStateException.class, () -> user.suspend());
        assertThrows(UserAlreadySuspendedException.class, () -> user.suspend());
    }

    @Test
    void cannotReactivateActiveUser() {
        User user = User.register("Ana", "3001234567", "Rionegro", "uid-123");

        assertThrows(IllegalStateException.class, () -> user.reactivate());
        assertThrows(UserNotSuspendedException.class, () -> user.reactivate());
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

    @Test
    void canOperateReturnsFalseForInactiveUser() {
        User user = User.register("Ana", "3001234567", "Rionegro", "uid-123");
        user.suspend();

        assertFalse(user.canOperate());
    }

    @Test
    void suspendedUserCanBeReactivated() {
        User user = User.register("Ana", "3001234567", "Rionegro", "uid-123");
        user.suspend();

        user.reactivate();

        assertEquals(State.ACTIVE, user.getState());
        assertTrue(user.canOperate());
    }

    private static Stream<Arguments> missingRequiredFields() {
        return Stream.of(
                Arguments.of(null, "3001234567", "Rionegro", "uid-123", "name"),
                Arguments.of("   ", "3001234567", "Rionegro", "uid-123", "name"),
                Arguments.of("Ana", null, "Rionegro", "uid-123", "phone"),
                Arguments.of("Ana", "   ", "Rionegro", "uid-123", "phone"),
                Arguments.of("Ana", "3001234567", null, "uid-123", "municipality"),
                Arguments.of("Ana", "3001234567", "   ", "uid-123", "municipality"),
                Arguments.of("Ana", "3001234567", "Rionegro", null, "externalId"),
                Arguments.of("Ana", "3001234567", "Rionegro", "   ", "externalId")
        );
    }
}
