package co.dazma.api.users.services.usecases;

import co.dazma.api.users.domain.UserNotFoundException;
import co.dazma.api.users.services.input.ports.ResolveUserEmailUseCase;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ResolveUserEmailService implements ResolveUserEmailUseCase {

    private final UserRepositoryPort userRepository;

    public ResolveUserEmailService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String resolveEmail(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new)
                .getEmail()
                .value();
    }
}
