package co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.repository;

import co.dazma.api.publications.infrastructure.output.repositories.structured.entities.PublicationStatusEntity;
import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.entities.PublicationEntityJPA;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataPublicationRepository extends JpaRepository<PublicationEntityJPA, UUID> {

    Slice<PublicationEntityJPA> findByStatus(PublicationStatusEntity status, Pageable pageable);

    Slice<PublicationEntityJPA> findByStatusAndMunicipality(
            PublicationStatusEntity status,
            String municipality,
            Pageable pageable
    );
}
