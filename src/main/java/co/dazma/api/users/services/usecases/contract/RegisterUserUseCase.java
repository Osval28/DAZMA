package co.dazma.api.users.services.usecases.contract;

import co.dazma.api.shared.services.usecases.UseCaseWithReturn;
import co.dazma.api.users.domain.models.User;
import co.dazma.api.users.domain.models.UserRegistration;

public interface RegisterUserUseCase extends UseCaseWithReturn<UserRegistration, User> {
}
