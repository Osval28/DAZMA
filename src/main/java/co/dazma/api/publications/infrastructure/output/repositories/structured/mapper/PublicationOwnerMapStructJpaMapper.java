package co.dazma.api.publications.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.publications.infrastructure.output.repositories.structured.entities.PublicationOwnerEntity;
import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.entities.PublicationOwnerEntityJPA;
import co.dazma.api.shared.infrastructure.JpaMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PublicationOwnerMapStructJpaMapper extends JpaMapper<PublicationOwnerEntityJPA, PublicationOwnerEntity> {

    @Override
    PublicationOwnerEntityJPA toJpaEntity(PublicationOwnerEntity entity);

    @Override
    @Mapping(target = "userName")
    PublicationOwnerEntity toEntity(PublicationOwnerEntityJPA jpaEntity);
}
