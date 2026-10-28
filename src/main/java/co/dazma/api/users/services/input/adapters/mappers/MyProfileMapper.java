package co.dazma.api.users.services.input.adapters.mappers;

import co.dazma.api.shared.services.input.mappers.OutputMapper;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.services.input.dto.GetMyProfileOutputDto;
import org.springframework.stereotype.Component;

@Component
public class MyProfileMapper implements OutputMapper<User, GetMyProfileOutputDto> {

    @Override
    public GetMyProfileOutputDto toOutput(User user) {
        return new GetMyProfileOutputDto(
                user.getId(),
                user.getName().value(),
                user.getPhone().number(),
                user.getMunicipality().name(),
                user.getEmail().value(),
                user.getState(),
                user.getRole()
        );
    }
}
