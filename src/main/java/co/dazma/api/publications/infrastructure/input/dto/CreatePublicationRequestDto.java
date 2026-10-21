package co.dazma.api.publications.infrastructure.input.dto;

import co.dazma.api.publications.domain.PublicationType;

import java.util.List;
import java.util.UUID;

public record CreatePublicationRequestDto(
        String name,
        PublicationType type,
        String description,
        String city,
        Integer price,
        List<UUID> photoUploadIds
) {
}
