package co.dazma.api.users.services.input.adapters.mappers;

import co.dazma.api.shared.domain.Municipality;
import co.dazma.api.shared.services.input.mappers.InputMapper;
import co.dazma.api.shared.services.input.mappers.OutputMapper;
import co.dazma.api.users.domain.Email;
import co.dazma.api.users.domain.Name;
import co.dazma.api.users.domain.Phone;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.domain.UserRegistration;
import co.dazma.api.users.services.input.dto.RegisterUserInputDto;
import co.dazma.api.users.services.input.dto.RegisterUserOutputDto;
import org.springframework.stereotype.Component;

@Component
public class RegisterUserMapper
        implements InputMapper<RegisterUserInputDto, UserRegistration>, OutputMapper<User, RegisterUserOutputDto> {

    @Override
    public UserRegistration toDomain(RegisterUserInputDto input) {
        return new UserRegistration(
                new Name(input.name()),
                new Phone(input.phone()),
                new Municipality(input.municipality()),
                input.externalId(),
                new Email(input.email())
        );
    }

    @Override
    public RegisterUserOutputDto toOutput(User user) {
        return new RegisterUserOutputDto(
                user.getId(),
                user.getName().value(),
                user.getPhone().number(),
                user.getMunicipality().name(),
                user.getState(),
                user.getRole()
        );
    }
}
