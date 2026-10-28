package co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.repository;

import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.entities.PublicationOwnerEntityJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataPublicationOwnerRepository extends JpaRepository<PublicationOwnerEntityJPA, UUID> {

    Optional<PublicationOwnerEntityJPA> findByExternalId(String externalId);
}
