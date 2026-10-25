package co.dazma.api.security.infrastructure.input.dto;

import jakarta.validation.constraints.NotBlank;

public record OpenSessionRequestDto(@NotBlank String idToken) {
}
