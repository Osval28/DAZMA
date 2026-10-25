package co.dazma.api.security.domain;

public record AuthenticatedIdentity(String uid, String email, boolean emailVerified) {
}
