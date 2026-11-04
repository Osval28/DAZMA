package co.dazma.api.users.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.shared.domain.models.Municipality;
import co.dazma.api.users.domain.models.Email;
import co.dazma.api.users.domain.models.Phone;
import co.dazma.api.users.domain.models.Name;
import co.dazma.api.shared.infrastructure.DomainMapper;
import co.dazma.api.users.domain.models.User;
import co.dazma.api.users.infrastructure.output.repositories.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapStructDomainMapper extends DomainMapper<UserEntity, User> {

    @Override
    default User toDomain(UserEntity entity) {
        return User.restore(entity.id(), new Name(entity.name()), new Phone(entity.phone()), new Municipality(entity.municipality())
                , entity.externalId(), entity.state(), entity.role(), new Email(entity.email()));
    }

    @Override
    UserEntity toEntity(User domain);

    default String map(Name name) {
        return name.value();
    }

    default String map(Phone phone) {
        return phone.number();
    }

    default String map(Municipality municipality) {
        return municipality.code();
    }

    default String map(Email email) {
        return email.value();
    }

}
