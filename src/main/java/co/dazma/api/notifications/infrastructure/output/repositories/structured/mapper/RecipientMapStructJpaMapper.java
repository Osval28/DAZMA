package co.dazma.api.notifications.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.notifications.infrastructure.output.repositories.structured.entities.RecipientEntity;
import co.dazma.api.notifications.infrastructure.output.repositories.structured.jpa.entities.RecipientEntityJPA;
import co.dazma.api.shared.infrastructure.JpaMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RecipientMapStructJpaMapper extends JpaMapper<RecipientEntityJPA, RecipientEntity> {

    @Override
    RecipientEntityJPA toJpaEntity(RecipientEntity entity);

    @Override
    RecipientEntity toEntity(RecipientEntityJPA jpaEntity);
}
