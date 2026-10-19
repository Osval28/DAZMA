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
    private final Integer price;

    private Publication(
            UUID id,
            UUID ownerId,
            String name,
            PublicationType type,
            String description,
            String city,
            PublicationStatus status,
            List<PublicationPhoto> photos,
            Instant createdAt,
            Integer price
    ) {
        requireValue(id, PublicationErrorCode.ID_REQUIRED);
        requireValue(ownerId, PublicationErrorCode.OWNER_REQUIRED);
        requireText(name, PublicationErrorCode.NAME_REQUIRED);
        requireValue(type, PublicationErrorCode.TYPE_REQUIRED);
        requireText(description, PublicationErrorCode.DESCRIPTION_REQUIRED);
        requireText(city, PublicationErrorCode.CITY_REQUIRED);
        requireValue(status, PublicationErrorCode.STATUS_REQUIRED);
        requireValue(createdAt, PublicationErrorCode.CREATED_AT_REQUIRED);
        requireValue(price, PublicationErrorCode.VALUE_REQUIRED);
        validatePhotos(photos);
        if (price <= 0) {
            throw new InvalidPublicationException(PublicationErrorCode.VALUE_NOT_POSITIVE);
        }
        this.id = id;
        this.ownerId = ownerId;
        this.name = name.trim();
        this.type = type;
        this.description = description.trim();
        this.city = city.trim();
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
            String city,
            List<PublicationPhoto> photos,
            Instant createdAt,
            Integer price
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
            String city,
            PublicationStatus status,
            List<PublicationPhoto> photos,
            Instant createdAt,
            Integer price
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
                createdAt,
                price
        );
    }

    private static void validatePhotos(List<PublicationPhoto> photos) {
        if (photos == null || photos.isEmpty()) {
            throw new InvalidPublicationException(PublicationErrorCode.PHOTOS_REQUIRED);
        }
        if (photos.stream().anyMatch(photo -> photo == null)) {
            throw new InvalidPublicationException(PublicationErrorCode.PHOTO_NULL);
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

    private static void requireValue(Object value, PublicationErrorCode errorCode) {
        if (value == null) {
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
    public Integer getPrice() {
        return price;
    }
}
