package co.dazma.api.users.infrastructure.input.dto;

import co.dazma.api.users.domain.User;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String name,
        String phone,
        String municipality,
        String state,
        String role
) {

    public static UserResponseDto fromModel(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getPhone(),
                user.getMunicipality(),
                user.getState().name(),
                user.getRole().name()
        );
    }
}