package co.dazma.api.security.services.usecases.contract;

import co.dazma.api.security.domain.models.Session;
import co.dazma.api.shared.services.usecases.UseCaseWithReturn;

public interface OpenSessionUseCase extends UseCaseWithReturn<String, Session> {
}
