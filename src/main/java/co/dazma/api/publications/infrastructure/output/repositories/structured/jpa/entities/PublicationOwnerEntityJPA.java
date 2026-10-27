package co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.entities;

import co.dazma.api.publications.infrastructure.output.repositories.structured.entities.PublicationOwnerStateEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "publication_owners", schema = "app_publications")
public class PublicationOwnerEntityJPA {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "external_id", nullable = false, unique = true)
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PublicationOwnerStateEntity state;

    protected PublicationOwnerEntityJPA() {}

    public PublicationOwnerEntityJPA(UUID userId, String externalId, PublicationOwnerStateEntity state) {
        this.userId = userId;
        this.externalId = externalId;
        this.state = state;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getExternalId() {
        return externalId;
    }

    public PublicationOwnerStateEntity getState() {
        return state;
    }
}
