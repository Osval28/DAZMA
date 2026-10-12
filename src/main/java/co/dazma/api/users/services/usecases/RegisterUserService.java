package co.dazma.api.users.services.usecases;

import co.dazma.api.users.domain.User;
import co.dazma.api.users.domain.UserAlreadyRegisteredException;
import co.dazma.api.users.services.input.ports.RegisterUserUseCase;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;

    public RegisterUserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(String name, String phone, String municipality, String externalId) {
        if (userRepository.findByExternalId(externalId).isPresent()) {
            throw new UserAlreadyRegisteredException();
        }

        User user = User.register(name, phone, municipality, externalId);

        return userRepository.save(user);
    }
}
