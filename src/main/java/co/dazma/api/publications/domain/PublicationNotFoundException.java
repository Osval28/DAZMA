package co.dazma.api.publications.domain;

import java.util.UUID;

public class PublicationNotFoundException extends RuntimeException {

    public PublicationNotFoundException(UUID publicationId) {
        super("No existe una publicación disponible con el identificador: " + publicationId);
    }
}
