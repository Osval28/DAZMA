package co.dazma.api.users.services.usecases;

import co.dazma.api.users.domain.User;
import co.dazma.api.users.domain.UserNotFoundException;
import co.dazma.api.users.services.input.dto.GetMyProfileInputDto;
import co.dazma.api.users.services.input.dto.GetMyProfileOutputDto;
import co.dazma.api.users.services.input.ports.GetMyProfileUseCase;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class GetMyProfileService implements GetMyProfileUseCase {

    private final UserRepositoryPort userRepository;

    public GetMyProfileService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public GetMyProfileOutputDto get(GetMyProfileInputDto input) {
        User user = userRepository.findByExternalId(input.externalId())
                .orElseThrow(UserNotFoundException::new);
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
