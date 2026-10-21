package co.dazma.api.users.services.usecases;

import co.dazma.api.users.domain.MissingRequiredFieldException;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.domain.UserCannotOperateException;
import co.dazma.api.users.domain.UserNotFoundException;
import co.dazma.api.users.services.input.ports.ResolveUserIdUseCase;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ResolveUserIdService implements ResolveUserIdUseCase {

    private final UserRepositoryPort userRepository;

    public ResolveUserIdService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UUID resolveByExternalId(String externalId) {
        if (externalId == null || externalId.isBlank()) {
            throw new MissingRequiredFieldException("externalId");
        }

        User user = userRepository.findByExternalId(externalId.trim())
                .orElseThrow(UserNotFoundException::new);
        if (!user.canOperate()) {
            throw new UserCannotOperateException();
        }
        return user.getId();
    }
}
