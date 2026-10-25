package co.dazma.api.security;

import org.springframework.security.core.AuthenticatedPrincipal;

public record AuthenticatedUser(String uid, String email, boolean emailVerified) implements AuthenticatedPrincipal {

    @Override
    public String getName() {
        return uid;
    }
}
