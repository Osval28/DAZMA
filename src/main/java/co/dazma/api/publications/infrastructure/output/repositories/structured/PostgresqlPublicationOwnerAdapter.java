package co.dazma.api.publications.infrastructure.output.repositories.structured;

import co.dazma.api.publications.domain.PublicationOwner;
import co.dazma.api.publications.infrastructure.output.repositories.structured.entities.PublicationOwnerEntity;
import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.entities.PublicationOwnerEntityJPA;
import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.repository.SpringDataPublicationOwnerRepository;
import co.dazma.api.publications.services.output.ports.PublicationOwnerRepositoryPort;
import co.dazma.api.shared.infrastructure.DomainMapper;
import co.dazma.api.shared.infrastructure.JpaMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PostgresqlPublicationOwnerAdapter implements PublicationOwnerRepositoryPort {

    private final SpringDataPublicationOwnerRepository springDataOwnerRepository;
    private final DomainMapper<PublicationOwnerEntity, PublicationOwner> domainMapper;
    private final JpaMapper<PublicationOwnerEntityJPA, PublicationOwnerEntity> jpaMapper;

    public PostgresqlPublicationOwnerAdapter(
            SpringDataPublicationOwnerRepository springDataOwnerRepository,
            DomainMapper<PublicationOwnerEntity, PublicationOwner> domainMapper,
            JpaMapper<PublicationOwnerEntityJPA, PublicationOwnerEntity> jpaMapper
    ) {
        this.springDataOwnerRepository = springDataOwnerRepository;
        this.domainMapper = domainMapper;
        this.jpaMapper = jpaMapper;
    }

    @Override
    public void save(PublicationOwner owner) {
        springDataOwnerRepository.save(jpaMapper.toJpaEntity(domainMapper.toEntity(owner)));
    }

    @Override
    public Optional<PublicationOwner> findByExternalId(String externalId) {
        return springDataOwnerRepository.findByExternalId(externalId)
                .map(jpaMapper::toEntity)
                .map(domainMapper::toDomain);
    }
}
