package co.dazma.api.publications.services.usecases.models;

import co.dazma.api.publications.domain.models.Publication;

public record PublicationSummary(Publication publication, PhotoAccess cover) {
}
