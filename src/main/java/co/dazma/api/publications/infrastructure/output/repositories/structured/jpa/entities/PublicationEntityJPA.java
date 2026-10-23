package co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.entities;

import co.dazma.api.publications.domain.PublicationStatus;
import co.dazma.api.publications.domain.PublicationType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "publications", schema = "app_publications")
public class PublicationEntityJPA {

    @Id
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PublicationType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String municipality;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PublicationStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "price", nullable = false)
    private Integer price;

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<PublicationPhotoEntityJPA> photos = new ArrayList<PublicationPhotoEntityJPA>();

    public void addPhoto(PublicationPhotoEntityJPA photo) {
        photo.assignTo(this);
        photos.add(photo);
    }

    public List<PublicationPhotoEntityJPA> getPhotos() {
        return photos;
    }
    protected PublicationEntityJPA() {
    }

    public PublicationEntityJPA(
            UUID id,
            UUID ownerId,
            String name,
            PublicationType type,
            String description,
            String municipality,
            PublicationStatus status,
            Instant createdAt,
            Integer price
    ) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.type = type;
        this.description = description;
        this.municipality = municipality;
        this.status = status;
        this.createdAt = createdAt;
        this.price = price;
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

    public String getMunicipality() {
        return municipality;
    }

    public PublicationStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Integer getPrice() {
        return price;
    }
}
