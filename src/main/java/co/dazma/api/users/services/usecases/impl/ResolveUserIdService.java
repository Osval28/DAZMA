package co.dazma.api.users.services.usecases.impl;

import co.dazma.api.users.domain.User;
import co.dazma.api.users.domain.UserCannotOperateExceptionDomain;
import co.dazma.api.users.domain.UserNotFoundException;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;
import co.dazma.api.users.services.usecases.contract.ResolveUserIdUseCase;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ResolveUserIdService implements ResolveUserIdUseCase {

    private final UserRepositoryPort userRepository;

    public ResolveUserIdService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UUID execute(String externalId) {
        User user = userRepository.findByExternalId(externalId)
                .orElseThrow(UserNotFoundException::new);
        if (!user.canOperate()) {
            throw new UserCannotOperateExceptionDomain();
        }
        return user.getId();
    }
}
