package co.dazma.api.users.infrastructure.input.dto;

import co.dazma.api.users.services.input.dto.RegisterUserOutputDto;

import java.util.UUID;

public record UserResponseDto(UUID id, String name, String phone, String municipality, String state, String role) {

    public static UserResponseDto from(RegisterUserOutputDto user) {
        return new UserResponseDto(
                user.id(),
                user.name(),
                user.phone(),
                user.municipality(),
                user.state().name(),
                user.role().name()
        );
    }
}
