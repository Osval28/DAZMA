package co.dazma.api.users.services.usecases.contract;

import co.dazma.api.shared.services.usecases.UseCaseWithReturn;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.domain.UserRegistration;

public interface RegisterUserUseCase extends UseCaseWithReturn<UserRegistration, User> {
}
