package co.dazma.api.security.services.input.dto;

public record AuthenticateIdentityOutputDto(String uid, String email, boolean emailVerified) {
}
