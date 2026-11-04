package co.dazma.api.users.infrastructure.output.repositories.entities;

import co.dazma.api.users.domain.enums.Role;
import co.dazma.api.users.domain.enums.State;

import java.util.UUID;

public record UserEntity (UUID id, String name, String phone, String municipality, String externalId, State state, Role role, String email) {

}
