package co.dazma.api.publications.services.output.ports;

import co.dazma.api.publications.domain.PublicationOwner;

import java.util.Optional;

public interface PublicationOwnerRepositoryPort {

    void save(PublicationOwner owner);

    Optional<PublicationOwner> findByExternalId(String externalId);
}
