package co.dazma.api.users.infrastructure.output.repositories.structured.entities;

import co.dazma.api.users.domain.Role;
import co.dazma.api.users.domain.State;

import java.util.UUID;

public record UserEntity (UUID id, String name, String phone, String municipality, String externalId, State state, Role role, Long version) {

}
