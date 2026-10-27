package co.dazma.api.users.infrastructure.output.events;

import co.dazma.api.users.events.UserRegistered;
import co.dazma.api.users.services.output.ports.UserEventsPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SpringUserEventsAdapter implements UserEventsPort {

    private final ApplicationEventPublisher eventPublisher;

    public SpringUserEventsAdapter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publish(UserRegistered event) {
        eventPublisher.publishEvent(event);
    }
}
