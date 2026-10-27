package co.dazma.api.security.domain;

public record TokenVerification(String token, boolean checkRevoked) {
}
