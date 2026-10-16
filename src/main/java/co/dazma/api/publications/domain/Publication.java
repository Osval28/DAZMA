package co.dazma.api.publications.domain;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Publication {

    private final UUID id;
    private final UUID ownerId;
    private final String name;
    private final PublicationType type;
    private final String description;
    private final String city;
    private final PublicationStatus status;
    private final List<PublicationPhoto> photos;
    private final Instant createdAt;

    private Publication(
            UUID id,
            UUID ownerId,
            String name,
            PublicationType type,
            String description,
            String city,
            PublicationStatus status,
            List<PublicationPhoto> photos,
            Instant createdAt
    ) {
        requireValue(id, "La publicación debe tener un identificador");
        requireValue(ownerId, "La publicación debe tener un propietario");
        requireText(name, "El nombre de la publicación es obligatorio");
        requireValue(type, "El tipo de publicación es obligatorio");
        requireText(description, "La descripción de la publicación es obligatoria");
        requireText(city, "La ciudad de la publicación es obligatoria");
        requireValue(status, "El estado de la publicación es obligatorio");
        requireValue(createdAt, "La fecha de creación es obligatoria");
        validatePhotos(photos);

        this.id = id;
        this.ownerId = ownerId;
        this.name = name.trim();
        this.type = type;
        this.description = description.trim();
        this.city = city.trim();
        this.status = status;
        this.photos = List.copyOf(photos);
        this.createdAt = createdAt;
    }

    public static Publication publish(
            UUID ownerId,
            String name,
            PublicationType type,
            String description,
            String city,
            List<PublicationPhoto> photos
    ) {
        return new Publication(
                UUID.randomUUID(),
                ownerId,
                name,
                type,
                description,
                city,
                PublicationStatus.PUBLISHED,
                photos,
                Instant.now()
        );
    }

    public static Publication restore(
            UUID id,
            UUID ownerId,
            String name,
            PublicationType type,
            String description,
            String city,
            PublicationStatus status,
            List<PublicationPhoto> photos,
            Instant createdAt
    ) {
        return new Publication(
                id,
                ownerId,
                name,
                type,
                description,
                city,
                status,
                photos,
                createdAt
        );
    }

    private static void validatePhotos(List<PublicationPhoto> photos) {
        if (photos == null || photos.isEmpty()) {
            throw new InvalidPublicationException("La publicación debe tener al menos una fotografía");
        }
        if (photos.stream().anyMatch(photo -> photo == null)) {
            throw new InvalidPublicationException("La lista de fotografías no puede contener valores nulos");
        }

        Set<UUID> photoIds = new HashSet<>();
        Set<Integer> positions = new HashSet<>();
        for (PublicationPhoto photo : photos) {
            if (!photoIds.add(photo.getId())) {
                throw new InvalidPublicationException("Una fotografía no puede repetirse en la publicación");
            }
            if (!positions.add(photo.getPosition())) {
                throw new InvalidPublicationException("Dos fotografías no pueden ocupar la misma posición");
            }
        }
    }

    private static void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new InvalidPublicationException(message);
        }
    }

    private static void requireValue(Object value, String message) {
        if (value == null) {
            throw new InvalidPublicationException(message);
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public PublicationType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getCity() {
        return city;
    }

    public PublicationStatus getStatus() {
        return status;
    }

    public List<PublicationPhoto> getPhotos() {
        return photos;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
