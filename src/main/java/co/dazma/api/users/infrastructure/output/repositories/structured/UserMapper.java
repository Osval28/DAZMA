package co.dazma.api.users.infrastructure.output.repositories.structured;

import co.dazma.api.users.domain.User;

public class UserMapper {

    public static UserEntity toEntity(User user) {
        return new UserEntity(user.getId(),user.getName(),user.getPhone(),user.getMunicipality(),user.getExternalId(),
                user.getRole(),user.getState());
    }

    public static User toModel(UserEntity userEntity) {
        return new User(userEntity.getId(), userEntity.getName(),userEntity.getPhone(),userEntity.getMunicipality(),userEntity.getState(),
                userEntity.getExternalId(),userEntity.getRole());
    }


}
