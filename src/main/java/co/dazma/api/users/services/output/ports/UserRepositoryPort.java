package co.dazma.api.users.services.output.ports;

import co.dazma.api.users.domain.models.User;

import java.util.Optional;

public interface UserRepositoryPort {

    Optional<User> findByExternalId(String externalId);
    User save(User user);
}
