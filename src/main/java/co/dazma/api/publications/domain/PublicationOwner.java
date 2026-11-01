package co.dazma.api.publications.domain;

import java.util.UUID;

public record PublicationOwner(UUID userId, String userName, String externalId, PublicationOwnerState state) {

    public PublicationOwner {
        if (externalId.isBlank()) {
            throw new InvalidPublicationException(PublicationErrorCode.OWNER_EXTERNAL_ID_REQUIRED);
        }
    }

    public boolean canOperate() {
        return state == PublicationOwnerState.ACTIVE;
    }
}
