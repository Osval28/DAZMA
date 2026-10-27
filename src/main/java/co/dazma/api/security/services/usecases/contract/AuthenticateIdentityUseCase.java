package co.dazma.api.security.services.usecases.contract;

import co.dazma.api.security.domain.AuthenticatedIdentity;
import co.dazma.api.security.domain.TokenVerification;
import co.dazma.api.shared.services.usecases.UseCaseWithReturn;

public interface AuthenticateIdentityUseCase extends UseCaseWithReturn<TokenVerification, AuthenticatedIdentity> {
}
