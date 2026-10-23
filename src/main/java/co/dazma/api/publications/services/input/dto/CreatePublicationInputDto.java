package co.dazma.api.publications.services.input.dto;

import co.dazma.api.publications.domain.PublicationType;

import java.util.List;
import java.util.UUID;

public record CreatePublicationInputDto(
        UUID ownerId,
        String name,
        PublicationType type,
        String description,
        String municipality,
        Integer price,
        List<UUID> photoIds
) {
}
