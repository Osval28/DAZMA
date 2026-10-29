package co.dazma.api.publications.infrastructure.output.repositories.structured;

import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.infrastructure.output.repositories.structured.entities.PublicationEntity;
import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.entities.PublicationEntityJPA;
import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.repository.SpringDataPublicationRepository;
import co.dazma.api.publications.infrastructure.output.repositories.structured.entities.PublicationStatusEntity;
import co.dazma.api.publications.services.output.models.PublishedPublicationsSlice;
import co.dazma.api.publications.services.output.ports.PublicationRepositoryPort;
import co.dazma.api.shared.domain.Municipality;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import co.dazma.api.shared.infrastructure.DomainMapper;
import co.dazma.api.shared.infrastructure.JpaMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        PublicationEntityJPA savedEntityJPA = springDataPublicationRepository.save(publicationEntityJPA);
        return domainMapper.toDomain(jpaMapper.toEntity(savedEntityJPA));
    }
    @Transactional(readOnly = true)
    @Override
    public Optional<Publication> findById(UUID publicationId) {
        return springDataPublicationRepository.findById(publicationId)
                .map(jpaMapper::toEntity).map(domainMapper::toDomain);
    }

    @Transactional(readOnly = true)
    @Override
    public PublishedPublicationsSlice findPublished(Optional<Municipality> municipality, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))
        );

        Slice<PublicationEntityJPA> slice = municipality
                .map(selected -> springDataPublicationRepository.findByStatusAndMunicipality(
                        PublicationStatusEntity.PUBLISHED, selected.code(), pageable))
                .orElseGet(() -> springDataPublicationRepository.findByStatus(
                        PublicationStatusEntity.PUBLISHED, pageable));

        List<Publication> publications = slice.getContent().stream()
                .map(jpaMapper::toEntity)
                .map(domainMapper::toDomain)
                .toList();

        return new PublishedPublicationsSlice(publications, slice.hasNext());
    }

}
