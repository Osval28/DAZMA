package co.dazma.api.publications.services.usecases.models;

import co.dazma.api.publications.domain.Publication;

/**
 * Una publicación tal como se muestra en el listado: sus datos y solo la foto de portada.
 */
public record PublicationSummary(Publication publication, PhotoAccess cover) {
}
