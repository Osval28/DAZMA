package co.dazma.api.publications.services.output.ports;

import co.dazma.api.publications.domain.Publication;

import java.util.Optional;
import java.util.UUID;

public interface PublicationRepositoryPort {

    Publication save(Publication publication);

    Optional<Publication> findById(UUID publicationId);
}
