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
    private final Municipality municipality;
    private final PublicationStatus status;
    private final List<PublicationPhoto> photos;
    private final Instant createdAt;
    private final Price price;

    private Publication(
            UUID id,
            UUID ownerId,
            String name,
            PublicationType type,
            String description,
            Municipality municipality,
            PublicationStatus status,
            List<PublicationPhoto> photos,
            Instant createdAt,
            Price price
    ) {
        requireText(name, PublicationErrorCode.NAME_REQUIRED);
        requireText(description, PublicationErrorCode.DESCRIPTION_REQUIRED);
        validatePhotos(photos);

        this.id = id;
        this.ownerId = ownerId;
        this.name = name.trim();
        this.type = type;
        this.description = description.trim();
        this.municipality = municipality;
        this.status = status;
        this.photos = List.copyOf(photos);
        this.createdAt = createdAt;
        this.price = price;
    }

    public static Publication publish(
            UUID ownerId,
            String name,
            PublicationType type,
            String description,
            Municipality municipality,
            List<PublicationPhoto> photos,
            Instant createdAt,
            Price price
    ) {
        return new Publication(
                UUID.randomUUID(),
                ownerId,
                name,
                type,
                description,
                municipality,
                PublicationStatus.PUBLISHED,
                photos,
                createdAt,
                price
        );
    }

    public static Publication restore(
            UUID id,
            UUID ownerId,
            String name,
            PublicationType type,
            String description,
            Municipality municipality,
            PublicationStatus status,
            List<PublicationPhoto> photos,
            Instant createdAt,
            Price price
    ) {
        return new Publication(
                id,
                ownerId,
                name,
                type,
                description,
                municipality,
                status,
                photos,
                createdAt,
                price
        );
    }

    private static void validatePhotos(List<PublicationPhoto> photos) {
        if (photos.isEmpty()) {
            throw new InvalidPublicationException(PublicationErrorCode.PHOTOS_REQUIRED);
        }


        Set<UUID> photoIds = new HashSet<>();
        Set<Integer> positions = new HashSet<>();
        for (PublicationPhoto photo : photos) {
            if (!photoIds.add(photo.getId())) {
                throw new InvalidPublicationException(PublicationErrorCode.PHOTO_DUPLICATED);
            }
            if (!positions.add(photo.getPosition())) {
                throw new InvalidPublicationException(PublicationErrorCode.PHOTO_POSITION_DUPLICATED);
            }
        }
    }

    private static void requireText(String value, PublicationErrorCode errorCode) {
        if (value == null || value.isBlank()) {
            throw new InvalidPublicationException(errorCode);
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

    public Municipality getMunicipality() {
        return municipality;
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

    public Price getPrice() {
        return price;
    }
}
