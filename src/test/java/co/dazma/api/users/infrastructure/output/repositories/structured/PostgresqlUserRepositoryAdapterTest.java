package co.dazma.api.users.infrastructure.output.repositories.structured;

import co.dazma.api.shared.domain.Municipality;
import co.dazma.api.users.domain.Email;
import co.dazma.api.users.domain.Phone;
import co.dazma.api.users.domain.Name;
import co.dazma.api.users.domain.Role;
import co.dazma.api.users.domain.State;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.infrastructure.output.repositories.structured.jpa.entities.UserEntityJPA;
import co.dazma.api.users.infrastructure.output.repositories.structured.jpa.repositories.SpringDataUserRepository;
import co.dazma.api.users.infrastructure.output.repositories.structured.mapper.UserMapStructDomainMapper;
import co.dazma.api.users.infrastructure.output.repositories.structured.mapper.UserMapStructJpaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostgresqlUserRepositoryAdapterTest {

    private SpringDataUserRepository springDataRepository;
    private PostgresqlUserRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        springDataRepository = mock(SpringDataUserRepository.class);
        adapter = new PostgresqlUserRepositoryAdapter(
                springDataRepository,
                Mappers.getMapper(UserMapStructDomainMapper.class),
                Mappers.getMapper(UserMapStructJpaMapper.class)
        );
    }

    @Test
    void shouldFindUserByExternalIdAndReturnDomainUser() {
        UUID id = UUID.randomUUID();
        UserEntityJPA entity = new UserEntityJPA(
                id,
                "Ana",
                "3001234567",
                "Rionegro",
                "firebase-123",
                State.ACTIVE,
                Role.USER,
                "ana@dazma.store"
        );
        when(springDataRepository.findByExternalId("firebase-123"))
                .thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findByExternalId("firebase-123");

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        assertEquals(new Name("Ana"), result.get().getName());
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
        User user = User.register(new Name("Ana"),
                new Phone("3001234567"),
                new Municipality("Rionegro"),
                "firebase-123",
                new Email("ana@dazma.store")
        );
        when(springDataRepository.save(any(UserEntityJPA.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = adapter.save(user);

        assertEquals(user.getId(), savedUser.getId());
        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getPhone(), savedUser.getPhone());
        assertEquals(user.getMunicipality(), savedUser.getMunicipality());
        assertEquals(user.getExternalId(), savedUser.getExternalId());
        assertEquals(user.getRole(), savedUser.getRole());
        assertEquals(user.getState(), savedUser.getState());
        assertEquals(user.getEmail(), savedUser.getEmail());
        verify(springDataRepository).save(any(UserEntityJPA.class));
    }

    @Test
    void shouldMapDomainUserToJpaEntityBeforeSaving() {
        User user = User.register(new Name("Ana"),
                new Phone("3001234567"),
                new Municipality("Rionegro"),
                "firebase-123",
                new Email("ana@dazma.store")
        );
        when(springDataRepository.save(any(UserEntityJPA.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        adapter.save(user);

        ArgumentCaptor<UserEntityJPA> captor = ArgumentCaptor.forClass(UserEntityJPA.class);
        verify(springDataRepository).save(captor.capture());
        UserEntityJPA saved = captor.getValue();
        assertEquals(user.getId(), saved.getId());
        assertEquals(user.getName().value(), saved.getName());
        assertEquals(user.getPhone().number(), saved.getPhone());
        assertEquals(user.getMunicipality().name(), saved.getMunicipality());
        assertEquals(user.getExternalId(), saved.getExternalId());
        assertEquals(user.getRole(), saved.getRole());
        assertEquals(user.getState(), saved.getState());
        assertEquals(user.getEmail().value(), saved.getEmail());
    }
}
