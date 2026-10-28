package co.dazma.api.users.services.input.dto;

import co.dazma.api.users.domain.Role;
import co.dazma.api.users.domain.State;

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
