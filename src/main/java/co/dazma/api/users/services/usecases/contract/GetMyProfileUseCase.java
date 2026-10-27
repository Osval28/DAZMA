package co.dazma.api.users.services.usecases.contract;

import co.dazma.api.shared.services.usecases.UseCaseWithReturn;
import co.dazma.api.users.domain.User;

public interface GetMyProfileUseCase extends UseCaseWithReturn<String, User> {
}
