package co.dazma.api.publications.infrastructure.output.repositories.structured;

import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.infrastructure.output.repositories.structured.entities.PublicationEntity;
import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.entities.PublicationEntityJPA;
import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.repository.SpringDataPublicationRepository;
import co.dazma.api.publications.services.output.ports.PublicationRepositoryPort;
import co.dazma.api.shared.infrastructure.DomainMapper;
import co.dazma.api.shared.infrastructure.JpaMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PostgresqlPublicationAdapter implements PublicationRepositoryPort {

    private final SpringDataPublicationRepository springDataPublicationRepository;
    private final DomainMapper<PublicationEntity,Publication> domainMapper;
    private final JpaMapper<PublicationEntityJPA,PublicationEntity> jpaMapper;


    public PostgresqlPublicationAdapter(SpringDataPublicationRepository springDataPublicationRepository, DomainMapper <PublicationEntity,
                                                Publication> domainMapper, JpaMapper<PublicationEntityJPA,PublicationEntity> jpaMapper) {

        this.springDataPublicationRepository = springDataPublicationRepository;
        this.domainMapper = domainMapper;
        this.jpaMapper = jpaMapper;
    }

    @Override
    public Publication save(Publication publication) {
        PublicationEntity  publicationEntity = domainMapper.toEntity(publication);
        PublicationEntityJPA publicationEntityJPA = jpaMapper.toJpaEntity(publicationEntity);
        springDataPublicationRepository.save(publicationEntityJPA);
        return domainMapper.toDomain(jpaMapper.toEntity(publicationEntityJPA));
    }
    @Transactional(readOnly = true)
    @Override
    public Optional<Publication> findById(UUID publicationId) {
        return springDataPublicationRepository.findById(publicationId)
                .map(jpaMapper::toEntity).map(domainMapper::toDomain);
    }


}
