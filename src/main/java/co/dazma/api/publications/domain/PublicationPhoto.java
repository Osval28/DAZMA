package co.dazma.api.publications.domain;

import java.util.UUID;

public class PublicationPhoto {

    private final UUID id;
    private final String objectKey;
    private final int position;

    public PublicationPhoto(UUID id, String objectKey, int position) {
        if (id == null) {
            throw new InvalidPublicationException("La fotografía debe tener un identificador");
        }
        if (objectKey == null || objectKey.isBlank()) {
            throw new InvalidPublicationException("La fotografía debe tener una referencia de almacenamiento");
        }
        if (position < 0) {
            throw new InvalidPublicationException("La posición de la fotografía no puede ser negativa");
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
