package co.dazma.api.users.services.usecases.contract;

import co.dazma.api.shared.services.usecases.UseCaseWithReturn;

import java.util.UUID;

public interface ResolveUserIdUseCase extends UseCaseWithReturn<String, UUID> {
}
