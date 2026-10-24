package co.dazma.api.users.domain;

import co.dazma.api.shared.domain.Municipality;

import java.util.UUID;

public class User {
    private final UUID id;
    private final Name name;
    private final Phone phone;
    private final Municipality municipality;
    private final String externalId;
    private State state;
    private final Role role;


    private User(UUID id, Name name, Phone phone, Municipality municipality, String externalId, State state, Role role) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.municipality = municipality;
        this.externalId = externalId;
        this.state = state;
        this.role = role;
    }

    public static User register(Name name, Phone phone, Municipality municipality, String externalId) {
        return new User(UUID.randomUUID(), name, phone, municipality, externalId, State.ACTIVE, Role.USER);
    }

    public static User restore(UUID id, Name name, Phone phone, Municipality municipality, String externalId, State state, Role role) {
        return new User(id, name, phone, municipality, externalId, state, role);
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new MissingRequiredFieldException(fieldName);
        }
    }

    public void suspend() {
        if (state == State.SUSPENDED) throw new UserAlreadySuspendedException();
        state = State.SUSPENDED;
    }

    public void reactivate() {
        if (state != State.SUSPENDED) throw new UserNotSuspendedException();
        state = State.ACTIVE;
    }

    public boolean canOperate()
    {
        return state == State.ACTIVE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public UUID getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Municipality getMunicipality() {
        return municipality;
    }

    public State getState() {
        return state;
    }

    public Role getRole() {
        return role;
    }

    public String getExternalId() {
        return externalId;
    }




}
