package co.dazma.api.notifications.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.notifications.domain.Recipient;
import co.dazma.api.notifications.infrastructure.output.repositories.structured.entities.RecipientEntity;
import co.dazma.api.shared.infrastructure.DomainMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RecipientMapStructDomainMapper extends DomainMapper<RecipientEntity, Recipient> {

    @Override
    Recipient toDomain(RecipientEntity entity);

    @Override
    RecipientEntity toEntity(Recipient domain);
}
