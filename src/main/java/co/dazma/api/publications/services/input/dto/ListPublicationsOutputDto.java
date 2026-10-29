package co.dazma.api.publications.services.input.dto;

import java.util.List;

public record ListPublicationsOutputDto(
        List<PublicationSummaryOutputDto> items,
        int page,
        int size,
        boolean hasNext
) {
}
