package co.dazma.api.publications.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.publications.domain.models.PublicationOwner;
import co.dazma.api.publications.infrastructure.output.repositories.entities.PublicationOwnerEntity;
import co.dazma.api.shared.infrastructure.DomainMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PublicationOwnerMapStructDomainMapper extends DomainMapper<PublicationOwnerEntity, PublicationOwner> {

    @Override
    PublicationOwner toDomain(PublicationOwnerEntity entity);

    @Override
    PublicationOwnerEntity toEntity(PublicationOwner domain);
}
