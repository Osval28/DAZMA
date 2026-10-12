package co.dazma.api.users.infrastructure.output.repositories.structured;

import co.dazma.api.users.domain.Role;
import co.dazma.api.users.domain.State;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users")

public class UserEntity {

    @Id
    private UUID id;

    @Column
    private String name;

    @Column
    private String phone;

    @Column
    private String municipality;

    @Column(unique = true)
    private String external_id;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private State state;

    protected UserEntity() {}

    public UserEntity(UUID id, String name, String phone, String municipality, String external_id, Role role, State state) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.municipality = municipality;
        this.external_id = external_id;
        this.role = role;
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


    public String getExternal_id() {
        return external_id;
    }


    public Role getRole() {
        return role;
    }

    public State getState() {
        return state;
    }


}