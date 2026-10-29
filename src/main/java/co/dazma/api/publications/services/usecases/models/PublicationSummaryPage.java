package co.dazma.api.publications.services.usecases.models;

import java.util.List;

public record PublicationSummaryPage(List<PublicationSummary> items, int page, int size, boolean hasNext) {

    public PublicationSummaryPage {
        items = List.copyOf(items);
    }
}
