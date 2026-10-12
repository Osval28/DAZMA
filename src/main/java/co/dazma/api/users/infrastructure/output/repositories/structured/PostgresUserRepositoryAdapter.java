package co.dazma.api.users.infrastructure.output.repositories.structured;

import co.dazma.api.users.domain.User;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public class PostgresUserRepositoryAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository springDataUserRepository;

    public PostgresUserRepositoryAdapter(SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
    }

    @Override
    public Optional<User> findByExternalId(String externalId) {
        return springDataUserRepository.findByExternalId(externalId)
                .map(UserMapper::fromEntitytoModel);
    }

    @Override
    public User save(User user) {
        UserEntity entityToSave = UserMapper.fromModeltoEntity(user);
        entityToSave = springDataUserRepository.save(entityToSave);
        return UserMapper.fromEntitytoModel(entityToSave);
    }
}
