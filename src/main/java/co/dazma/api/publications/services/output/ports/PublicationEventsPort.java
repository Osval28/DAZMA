package co.dazma.api.publications.services.output.ports;

import co.dazma.api.publications.events.PublicationCreated;

public interface PublicationEventsPort {

    void publish(PublicationCreated event);
}
