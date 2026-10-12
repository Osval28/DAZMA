package co.dazma.api.users.infrastructure.output.repositories.structured;

import co.dazma.api.users.domain.Role;
import co.dazma.api.users.domain.State;
import co.dazma.api.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostgresUserRepositoryAdapterTest {

    private SpringDataUserRepository springDataRepository;
    private PostgresUserRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        springDataRepository = mock(SpringDataUserRepository.class);
        adapter = new PostgresUserRepositoryAdapter(springDataRepository);
    }

    @Test
    void shouldFindUserByExternalIdAndReturnDomainUser() {
        UUID id = UUID.randomUUID();
        UserEntity entity = new UserEntity(
                id,
                "Ana",
                "3001234567",
                "Rionegro",
                "firebase-123",
                Role.USER,
                State.ACTIVE
        );
        when(springDataRepository.findByExternalId("firebase-123"))
                .thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findByExternalId("firebase-123");

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        assertEquals("Ana", result.get().getName());
        assertEquals("firebase-123", result.get().getExternalId());
        assertEquals(Role.USER, result.get().getRole());
        assertEquals(State.ACTIVE, result.get().getState());
        verify(springDataRepository).findByExternalId("firebase-123");
    }

    @Test
    void shouldReturnEmptyWhenExternalIdDoesNotExist() {
        when(springDataRepository.findByExternalId("unknown"))
                .thenReturn(Optional.empty());

        Optional<User> result = adapter.findByExternalId("unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldConvertDomainUserSaveItAndReturnTheSavedUser() {
        User user = User.register(
                "Ana",
                "3001234567",
                "Rionegro",
                "firebase-123"
        );
        when(springDataRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = adapter.save(user);

        assertEquals(user.getId(), savedUser.getId());
        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getPhone(), savedUser.getPhone());
        assertEquals(user.getMunicipality(), savedUser.getMunicipality());
        assertEquals(user.getExternalId(), savedUser.getExternalId());
        assertEquals(user.getRole(), savedUser.getRole());
        assertEquals(user.getState(), savedUser.getState());
        verify(springDataRepository).save(any(UserEntity.class));
    }
}
