package co.dazma.api.users.infrastructure.input.dto;

import co.dazma.api.users.services.input.dto.GetMyProfileOutputDto;

import java.util.UUID;

public record MyProfileResponseDto(
        UUID id,
        String name,
        String phone,
        String municipality,
        String email,
        boolean emailVerified,
        String state,
        String role
) {
    public static MyProfileResponseDto from(GetMyProfileOutputDto profile, boolean emailVerified) {
        return new MyProfileResponseDto(
                profile.id(),
                profile.name(),
                profile.phone(),
                profile.municipality(),
                profile.email(),
                emailVerified,
                profile.state().name(),
                profile.role().name()
        );
    }
}
