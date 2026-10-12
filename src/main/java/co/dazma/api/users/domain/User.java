package co.dazma.api.users.domain;

import java.util.UUID;

public class User {
    private final UUID id;
    private String name;
    private String phone;
    private String municipality;
    private final String externalId;
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

    public static User register(String name, String phone, String municipality, String externalId) {
        requireText(externalId, "externalId");
        requireText(name, "name");
        requireText(phone, "phone");
        requireText(municipality, "municipality");
        return new User(UUID.randomUUID(), name, phone, municipality, State.ACTIVE, externalId, Role.USER);
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
    {return state == State.ACTIVE; }

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

    public State getState() {
        return state;
    }

    public Role getRole() {
        return role;
    }

    public String getExternalId() {
        return externalId;
    }

    public void deactivate() {
        suspend();
    }

}
