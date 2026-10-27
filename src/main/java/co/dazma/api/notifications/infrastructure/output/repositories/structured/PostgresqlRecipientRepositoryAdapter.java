package co.dazma.api.notifications.infrastructure.output.repositories.structured;

import co.dazma.api.notifications.domain.Recipient;
import co.dazma.api.notifications.infrastructure.output.repositories.structured.entities.RecipientEntity;
import co.dazma.api.notifications.infrastructure.output.repositories.structured.jpa.entities.RecipientEntityJPA;
import co.dazma.api.notifications.infrastructure.output.repositories.structured.jpa.repositories.SpringDataRecipientRepository;
import co.dazma.api.notifications.services.output.ports.RecipientRepositoryPort;
import co.dazma.api.shared.infrastructure.DomainMapper;
import co.dazma.api.shared.infrastructure.JpaMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PostgresqlRecipientRepositoryAdapter implements RecipientRepositoryPort {

    private final SpringDataRecipientRepository springDataRecipientRepository;
    private final DomainMapper<RecipientEntity, Recipient> domainMapper;
    private final JpaMapper<RecipientEntityJPA, RecipientEntity> jpaMapper;

    public PostgresqlRecipientRepositoryAdapter(
            SpringDataRecipientRepository springDataRecipientRepository,
            DomainMapper<RecipientEntity, Recipient> domainMapper,
            JpaMapper<RecipientEntityJPA, RecipientEntity> jpaMapper
    ) {
        this.springDataRecipientRepository = springDataRecipientRepository;
        this.domainMapper = domainMapper;
        this.jpaMapper = jpaMapper;
    }

    @Override
    public void save(Recipient recipient) {
        springDataRecipientRepository.save(jpaMapper.toJpaEntity(domainMapper.toEntity(recipient)));
    }

    @Override
    public Optional<Recipient> findByUserId(UUID userId) {
        return springDataRecipientRepository.findById(userId)
                .map(jpaMapper::toEntity)
                .map(domainMapper::toDomain);
    }
}
