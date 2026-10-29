package co.dazma.api.publications.infrastructure.input.dto;

import java.util.List;

public record ListPublicationsResponseDto(
        List<PublicationSummaryResponseDto> items,
        int page,
        int size,
        boolean hasNext
) {
}
