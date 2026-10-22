package co.dazma.api.users.infrastructure.output.repositories.structured.jpa.entities;

import co.dazma.api.users.domain.Role;
import co.dazma.api.users.domain.State;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users", schema = "app_users")

public class UserEntityJPA {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String municipality;

    @Column(name = "external_id", nullable = false, unique = true)
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    protected UserEntityJPA() {}

    public UserEntityJPA(UUID id, String name, String phone, String municipality, String externalId, State state, Role role) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.municipality = municipality;
        this.externalId = externalId;
        this.state = state;
        this.role = role;
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
