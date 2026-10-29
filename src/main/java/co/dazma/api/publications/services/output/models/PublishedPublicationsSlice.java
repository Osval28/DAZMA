package co.dazma.api.publications.services.output.models;

import co.dazma.api.publications.domain.Publication;

import java.util.List;

/**
 * Una porción de publicaciones activas y si existe otra después de ella.
 * No incluye el total: contarlo obliga a recorrer toda la tabla en cada petición.
 */
public record PublishedPublicationsSlice(List<Publication> publications, boolean hasNext) {

    public PublishedPublicationsSlice {
        publications = List.copyOf(publications);
    }
}
