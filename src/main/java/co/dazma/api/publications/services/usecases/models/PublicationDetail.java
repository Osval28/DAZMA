package co.dazma.api.publications.services.usecases.models;

import co.dazma.api.publications.domain.Publication;

import java.util.List;

public record PublicationDetail(Publication publication, List<PhotoAccess> photos) {

    public PublicationDetail {
        photos = List.copyOf(photos);
    }
}
