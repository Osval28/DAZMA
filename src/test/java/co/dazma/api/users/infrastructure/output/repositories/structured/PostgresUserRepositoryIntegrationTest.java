package co.dazma.api.users.infrastructure.output.repositories.structured;

import co.dazma.api.TestcontainersConfiguration;
import co.dazma.api.users.domain.Role;
import co.dazma.api.users.domain.State;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class PostgresUserRepositoryIntegrationTest {

    @Autowired
    private UserRepositoryPort userRepository;

    @Autowired
    private SpringDataUserRepository springDataRepository;

    @Test
    void shouldPersistAndFindAUserUsingPostgres() {
        User user = User.register(
                "Ana",
                "3001234567",
                "Rionegro",
                "firebase-integration-123"
        );

        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findByExternalId(
                "firebase-integration-123"
        );

        assertEquals(user.getId(), savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Ana", foundUser.get().getName());
        assertEquals("3001234567", foundUser.get().getPhone());
        assertEquals("Rionegro", foundUser.get().getMunicipality());
        assertEquals(Role.USER, foundUser.get().getRole());
        assertEquals(State.ACTIVE, foundUser.get().getState());
    }

    @Test
    void shouldRejectDuplicatedExternalIds() {
        UserEntity firstUser = new UserEntity(
                UUID.randomUUID(),
                "Ana",
                "3001234567",
                "Rionegro",
                "duplicated-firebase-id",
                Role.USER,
                State.ACTIVE
        );
        UserEntity secondUser = new UserEntity(
                UUID.randomUUID(),
                "Luis",
                "3017654321",
                "Medellin",
                "duplicated-firebase-id",
                Role.USER,
                State.ACTIVE
        );
        springDataRepository.saveAndFlush(firstUser);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> springDataRepository.saveAndFlush(secondUser)
        );
    }
}
