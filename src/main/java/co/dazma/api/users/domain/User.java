package co.dazma.api.users.domain;

import java.util.UUID;

public class User {
    private UUID id;
    private String name;
    private String phone;
    private String municipality;
    private String externalId;
    private State state;
    private Role role;


    public User(UUID id, String name, String phone, String municipality, State state, String externalId, Role role) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.municipality = municipality;
        this.state = state;
        this.role = role;
        this.externalId = externalId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public State getEstado() {
        return state;
    }

    public void setEstado(State state) {
        this.state = state;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
