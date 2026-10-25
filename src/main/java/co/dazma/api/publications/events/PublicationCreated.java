package co.dazma.api.publications.events;

import java.time.Instant;
import java.util.UUID;

public record PublicationCreated(
        UUID publicationId,
        UUID ownerId,
        Instant createdAt
) {
}
