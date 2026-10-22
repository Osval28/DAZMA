package co.dazma.api.users.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.shared.infrastructure.DomainMapper;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.infrastructure.output.repositories.structured.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapStructDomainMapper extends DomainMapper<UserEntity, User> {

    @Override
    default User toDomain(UserEntity entity) {
        return User.restore(entity.id(), entity.name(), entity.phone(), entity.municipality(), entity.externalId(), entity.state(), entity.role());
    }

    @Override
    UserEntity toEntity(User domain);

}
