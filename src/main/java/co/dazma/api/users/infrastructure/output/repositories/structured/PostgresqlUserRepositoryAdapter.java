package co.dazma.api.users.infrastructure.output.repositories.structured;

import co.dazma.api.users.domain.User;
import co.dazma.api.users.infrastructure.output.repositories.structured.entities.UserEntity;
import co.dazma.api.users.infrastructure.output.repositories.structured.jpa.entities.UserEntityJPA;
import co.dazma.api.users.infrastructure.output.repositories.structured.jpa.repositories.SpringDataUserRepository;
import co.dazma.api.shared.infrastructure.DomainMapper;
import co.dazma.api.shared.infrastructure.JpaMapper;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PostgresqlUserRepositoryAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository springDataUserRepository;
    private final DomainMapper<UserEntity, User> domainMapper;
    private final JpaMapper <UserEntityJPA, UserEntity> jpaMapper;

    public PostgresqlUserRepositoryAdapter(SpringDataUserRepository springDataUserRepository,
                                           DomainMapper<UserEntity, User> domainMapper, JpaMapper<UserEntityJPA,
                                         UserEntity> jpaMapper) {
        this.springDataUserRepository = springDataUserRepository;
        this.domainMapper = domainMapper;
        this.jpaMapper = jpaMapper;
    }

    @Override
    public Optional<User> findByExternalId(String externalId) {
        return springDataUserRepository.findByExternalId(externalId)
                .map(jpaMapper::toEntity)
                .map(domainMapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntityJPA entityToSave = jpaMapper.toJpaEntity(domainMapper.toEntity(user));
        entityToSave = springDataUserRepository.save(entityToSave);
        UserEntity entity = jpaMapper.toEntity(entityToSave);
        return domainMapper.toDomain(entity);
    }
}
