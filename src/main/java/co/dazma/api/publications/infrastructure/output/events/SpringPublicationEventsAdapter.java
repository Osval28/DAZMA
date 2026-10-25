package co.dazma.api.publications.infrastructure.output.events;

import co.dazma.api.publications.events.PublicationCreated;
import co.dazma.api.publications.services.output.ports.PublicationEventsPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SpringPublicationEventsAdapter implements PublicationEventsPort {

    private final ApplicationEventPublisher eventPublisher;

    public SpringPublicationEventsAdapter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publish(PublicationCreated event) {
        eventPublisher.publishEvent(event);
    }
}
