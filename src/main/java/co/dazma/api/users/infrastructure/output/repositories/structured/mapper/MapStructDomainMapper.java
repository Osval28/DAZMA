package co.dazma.api.users.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.shared.infrastructure.DomainMapper;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.infrastructure.output.repositories.structured.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MapStructDomainMapper extends DomainMapper<UserEntity, User> {

    @Override
    User toDomain(UserEntity entity);

    @Override
    UserEntity toEntity(User domain);

}
