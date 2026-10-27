package co.dazma.api.users.services.usecases.impl;

import co.dazma.api.users.domain.User;
import co.dazma.api.users.domain.UserNotFoundException;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;
import co.dazma.api.users.services.usecases.contract.GetMyProfileUseCase;
import org.springframework.stereotype.Service;

@Service
public class GetMyProfileService implements GetMyProfileUseCase {

    private final UserRepositoryPort userRepository;

    public GetMyProfileService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User execute(String externalId) {
        return userRepository.findByExternalId(externalId)
                .orElseThrow(UserNotFoundException::new);
    }
}
