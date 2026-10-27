package co.dazma.api.users.services.output.ports;

import co.dazma.api.users.events.UserRegistered;

public interface UserEventsPort {

    void publish(UserRegistered event);
}
