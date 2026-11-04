package co.dazma.api.users.services.input.dto;

import co.dazma.api.users.domain.enums.Role;
import co.dazma.api.users.domain.enums.State;

import java.util.UUID;

public record RegisterUserOutputDto(
        UUID id,
        String name,
        String phone,
        String municipality,
        State state,
        Role role
) {
}
