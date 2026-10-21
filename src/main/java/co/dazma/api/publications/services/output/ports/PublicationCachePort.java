package co.dazma.api.publications.services.output.ports;

import co.dazma.api.publications.domain.Publication;

import java.util.Optional;
import java.util.UUID;

public interface PublicationCachePort {

    Optional<Publication> findById(UUID publicationId);

    void save(Publication publication);

    void evict(UUID publicationId);
}