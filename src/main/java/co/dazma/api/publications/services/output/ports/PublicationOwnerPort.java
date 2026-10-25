package co.dazma.api.publications.services.output.ports;

import java.util.UUID;

public interface PublicationOwnerPort {

    UUID resolveOperatingOwner(String externalId);
}
