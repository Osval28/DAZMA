package co.dazma.api.publications.services.input.dto;

import java.util.Set;

public record PublicationRulesOutputDto(
        int maximumPhotos,
        int maximumPageSize,
        int defaultPageSize,
        Set<String> allowedPhotoContentTypes,
        long maximumPhotoBytes
) {
}
