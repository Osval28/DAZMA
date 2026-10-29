package co.dazma.api.publications.services.output.ports;

import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.services.output.models.PublishedPublicationsSlice;
import co.dazma.api.shared.domain.Municipality;

import java.util.Optional;
import java.util.UUID;

public interface PublicationRepositoryPort {

    Publication save(Publication publication);

    Optional<Publication> findById(UUID publicationId);

    /**
     * Publicaciones activas, de la más reciente a la más antigua, opcionalmente de un solo municipio.
     * {@code page} empieza en 0.
     */
    PublishedPublicationsSlice findPublished(Optional<Municipality> municipality, int page, int size);
}
