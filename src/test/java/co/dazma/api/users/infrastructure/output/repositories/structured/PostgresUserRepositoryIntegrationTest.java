package co.dazma.api.users.infrastructure.output.repositories.structured;

import co.dazma.api.shared.domain.Municipality;
import co.dazma.api.users.domain.Email;
import co.dazma.api.users.domain.Phone;
import co.dazma.api.users.domain.Name;
import co.dazma.api.TestcontainersConfiguration;
import co.dazma.api.users.domain.Role;
import co.dazma.api.users.domain.State;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.infrastructure.output.repositories.structured.jpa.entities.UserEntityJPA;
import co.dazma.api.users.infrastructure.output.repositories.structured.jpa.repositories.SpringDataUserRepository;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class PostgresUserRepositoryIntegrationTest {

    @Autowired
    private UserRepositoryPort userRepository;

    @Autowired
    private SpringDataUserRepository springDataRepository;

    @Test
    void shouldPersistAndFindAUserUsingPostgres() {
        User user = User.register(new Name("Ana"),
                new Phone("3001234567"),
                new Municipality("Rionegro"),
                "firebase-integration-123",
                new Email("ana@dazma.store")
        );

        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findByExternalId(
                "firebase-integration-123"
        );

        assertEquals(user.getId(), savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Ana", foundUser.get().getName().value());
        assertEquals("3001234567", foundUser.get().getPhone().number());
        assertEquals("Rionegro", foundUser.get().getMunicipality().name());
        assertEquals(Role.USER, foundUser.get().getRole());
        assertEquals(State.ACTIVE, foundUser.get().getState());
    }

    @Test
    void shouldRejectDuplicatedExternalIds() {
        UserEntityJPA firstUser = new UserEntityJPA(
                UUID.randomUUID(),
                "Ana",
                "3001234567",
                "Rionegro",
                "duplicated-firebase-id",
                State.ACTIVE,
                Role.USER,
                "ana@dazma.store"
        );
        UserEntityJPA secondUser = new UserEntityJPA(
                UUID.randomUUID(),
                "Luis",
                "3017654321",
                "Medellin",
                "duplicated-firebase-id",
                State.ACTIVE,
                Role.USER,
                "luis@dazma.store"
        );
        springDataRepository.saveAndFlush(firstUser);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> springDataRepository.saveAndFlush(secondUser)
        );
    }
}
