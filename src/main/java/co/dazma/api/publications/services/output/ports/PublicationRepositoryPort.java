package co.dazma.api.publications.services.output.ports;

import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.services.output.models.PublishedPublicationsSlice;
import co.dazma.api.shared.domain.Municipality;

import java.util.Optional;
import java.util.UUID;

public interface PublicationRepositoryPort {

    Publication save(Publication publication);

    Optional<Publication> findById(UUID publicationId);

    PublishedPublicationsSlice findPublished(Optional<Municipality> municipality, int page, int size);
}
