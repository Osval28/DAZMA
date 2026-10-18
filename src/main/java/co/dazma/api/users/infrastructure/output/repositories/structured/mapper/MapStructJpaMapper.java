package co.dazma.api.users.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.users.infrastructure.output.repositories.structured.entities.UserEntity;
import co.dazma.api.users.infrastructure.output.repositories.structured.jpa.entities.UserEntityJPA;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapStructJpaMapper extends JpaMapper<UserEntityJPA, UserEntity> {

    @Override
    UserEntity toEntity(UserEntityJPA jpaEntity);

    @Override
    UserEntityJPA toJpaEntity(UserEntity entity);

}
