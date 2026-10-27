package co.dazma.api.publications.domain;

import co.dazma.api.shared.domain.Municipality;

import java.util.List;
import java.util.UUID;

public record PublicationDraft(
        String ownerExternalId,
        boolean ownerEmailVerified,
        Name name,
        PublicationType type,
        Description description,
        Municipality municipality,
        Price price,
        List<UUID> photoIds
) {

    public PublicationDraft {
        photoIds = List.copyOf(photoIds);
    }
}
