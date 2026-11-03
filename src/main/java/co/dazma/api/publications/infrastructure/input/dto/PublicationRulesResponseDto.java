package co.dazma.api.publications.infrastructure.input.dto;

import java.util.Set;

public record PublicationRulesResponseDto(
        int maximumPhotos,
        int maximumPageSize,
        int defaultPageSize,
        Set<String> allowedPhotoContentTypes,
        long maximumPhotoBytes
) {
}
