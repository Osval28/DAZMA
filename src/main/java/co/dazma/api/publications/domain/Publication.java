package co.dazma.api.publications.domain;
import co.dazma.api.shared.domain.Municipality;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Publication {

    private final UUID id;
    private final UUID ownerId;
    private final Name name;
    private final PublicationType type;
    private final Description description;
    private final Municipality municipality;
    private final PublicationStatus status;
    private final List<PublicationPhoto> photos;
    private final Instant createdAt;
    private final Price price;
    private final Long version;

    private Publication(
            UUID id,
            UUID ownerId,
            Name name,
            PublicationType type,
            Description description,
            Municipality municipality,
            PublicationStatus status,
            List<PublicationPhoto> photos,
            Instant createdAt,
            Price price,
            Long version
    ) {
        validatePhotos(photos);

        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.type = type;
        this.description = description;
        this.municipality = municipality;
        this.status = status;
        this.photos = List.copyOf(photos);
        this.createdAt = createdAt;
        this.price = price;
        this.version = version;
    }

    public static Publication publish(
            UUID ownerId,
            Name name,
            PublicationType type,
            Description description,
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
                price,
                null
        );
    }

    public static Publication restore(
            UUID id,
            UUID ownerId,
            Name name,
            PublicationType type,
            Description description,
            Municipality municipality,
            PublicationStatus status,
            List<PublicationPhoto> photos,
            Instant createdAt,
            Price price,
            Long version
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
                price,
                version
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

    public UUID getId()  {
        return id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public Name getName() {
        return name;
    }

    public PublicationType getType() {
        return type;
    }

    public Description getDescription() {
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

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Publication publication)) return false;
        return id.equals(publication.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
