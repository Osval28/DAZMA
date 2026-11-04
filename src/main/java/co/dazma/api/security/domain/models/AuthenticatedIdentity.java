package co.dazma.api.security.domain.models;

public record AuthenticatedIdentity(String uid, String email, boolean emailVerified) {
}
