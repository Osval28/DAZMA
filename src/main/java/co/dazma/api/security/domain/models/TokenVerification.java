package co.dazma.api.security.domain.models;

public record TokenVerification(String token, boolean checkRevoked) {
}
