package co.dazma.api.publications.domain;

import java.util.UUID;

public class PublicationPhoto {

    private final UUID id;
    private final String objectKey;
    private final int position;

    public PublicationPhoto(UUID id, String objectKey, int position) {
        if (id == null) {
            throw new InvalidPublicationException(PublicationErrorCode.PHOTO_ID_REQUIRED);
        }
        if (objectKey == null || objectKey.isBlank()) {
            throw new InvalidPublicationException(PublicationErrorCode.PHOTO_STORAGE_REFERENCE_REQUIRED);
        }
        if (position < 0) {
            throw new InvalidPublicationException(PublicationErrorCode.PHOTO_POSITION_INVALID);
        }

        this.id = id;
        this.objectKey = objectKey;
        this.position = position;
    }

    public UUID getId() {
        return id;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public int getPosition() {
        return position;
    }
}
