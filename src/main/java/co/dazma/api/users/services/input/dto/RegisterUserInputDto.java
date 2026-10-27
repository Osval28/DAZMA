package co.dazma.api.users.services.input.dto;

public record RegisterUserInputDto(
        String name,
        String phone,
        String municipality,
        String externalId,
        String email
) {
}
