package co.dazma.api.publications.domain;

import java.util.UUID;

public record PublicationOwner(UUID userId, String externalId, PublicationOwnerState state) {

    public PublicationOwner {
        if (userId == null) {
            throw new InvalidPublicationException(PublicationErrorCode.OWNER_REQUIRED);
        }
        if (externalId == null || externalId.isBlank()) {
            throw new InvalidPublicationException(PublicationErrorCode.OWNER_EXTERNAL_ID_REQUIRED);
        }
        if (state == null) {
            throw new InvalidPublicationException(PublicationErrorCode.OWNER_STATE_REQUIRED);
        }
    }

    public boolean canOperate() {
        return state == PublicationOwnerState.ACTIVE;
    }
}
