package co.dazma.api.users.infrastructure.output.repositories.structured.entities;

import co.dazma.api.users.domain.Role;
import co.dazma.api.users.domain.State;

import java.util.UUID;

public class UserEntity {

    private UUID id;
    private String name;
    private String phone;
    private String municipality;
    private String externalId;
    private State state;
    private Role role;

    public UserEntity(UUID id, String name, String phone,
                      String municipality,String externalId, State state, Role role) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.municipality = municipality;
        this.role = role;
        this.externalId = externalId;
        this.state = state;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getMunicipality() {
        return municipality;
    }

    public String getExternalId() {
        return externalId;
    }

    public Role getRole() {
        return role;
    }

    public State getState() {
        return state;
    }
}
