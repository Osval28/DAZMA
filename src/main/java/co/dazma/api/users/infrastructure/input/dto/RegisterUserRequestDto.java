package co.dazma.api.users.infrastructure.input.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequestDto(@NotBlank String name, @NotBlank String phone, @NotBlank String municipality){

}
