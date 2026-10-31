package co.dazma.api.publications.services.output.models;

import co.dazma.api.publications.domain.Publication;

import java.util.List;

public record PublishedPublicationsSlice(List<Publication> publications, boolean hasNext) {

    public PublishedPublicationsSlice {
        publications = List.copyOf(publications);
    }
}
