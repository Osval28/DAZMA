package co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "publication_photos", schema = "app_publications")
public class PublicationPhotoEntityJPA {

    @Id
    private UUID id;

    @Column(name = "object_key", nullable = false, length = 1024)
    private String objectKey;

    @Column(name = "display_order", nullable = false)
    private int position;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publication_id", nullable = false)
    private PublicationEntityJPA publication;

    protected PublicationPhotoEntityJPA() {}

    public PublicationPhotoEntityJPA(UUID id, String objectKey, int position) {
        this.id = id;
        this.objectKey = objectKey;
        this.position = position;
    }

    void assignTo(PublicationEntityJPA publication) { this.publication = publication; }

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
