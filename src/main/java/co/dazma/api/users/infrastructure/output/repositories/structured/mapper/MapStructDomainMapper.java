package co.dazma.api.users.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.users.domain.User;
import co.dazma.api.users.infrastructure.output.repositories.structured.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapStructDomainMapper extends DomainMapper<UserEntity, User> {

    @Override
    User toDomain(UserEntity entity);

    @Override
    UserEntity toEntity(User domain);

}
