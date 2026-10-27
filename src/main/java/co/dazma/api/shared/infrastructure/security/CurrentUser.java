package co.dazma.api.shared.infrastructure.security;

import org.springframework.security.core.AuthenticatedPrincipal;

public record CurrentUser(String uid, String email, boolean emailVerified) implements AuthenticatedPrincipal {

    @Override
    public String getName() {
        return uid;
    }
}
