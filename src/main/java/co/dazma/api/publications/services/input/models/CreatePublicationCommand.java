package co.dazma.api.publications.services.input.models;

import co.dazma.api.publications.domain.PublicationType;

import java.util.List;
import java.util.UUID;

public record CreatePublicationCommand(
        UUID ownerId,
        String name,
        PublicationType type,
        String description,
        String city,
        Integer price,
        List<UUID> photoIds
) {
}
