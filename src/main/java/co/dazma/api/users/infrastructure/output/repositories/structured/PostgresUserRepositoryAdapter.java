package co.dazma.api.users.infrastructure.output.repositories.structured;

import co.dazma.api.users.domain.User;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;

import java.util.Optional;

public class PostgresUserRepositoryAdapter implements UserRepositoryPort {

    @Override
    public Optional<User> findByExternalId(String externalId) {
        return
    }

    @Override
    public User save(User user) {
        return null;
    }
}
