package co.dazma.api.security.domain;

import co.dazma.api.shared.domain.errors.DomainUnauthenticatedException;

public class RecentSignInRequiredException extends DomainUnauthenticatedException {

    public RecentSignInRequiredException() {
        super(SecurityErrorCode.RECENT_SIGN_IN_REQUIRED);
    }
}
