package co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.repository;

import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.entities.PublicationEntityJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataPublicationRepository extends JpaRepository<PublicationEntityJPA, UUID> {
}
