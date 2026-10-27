package co.dazma.api.security.services.input.dto;

public record AuthenticateIdentityInputDto(String token, boolean checkRevoked) {
}
