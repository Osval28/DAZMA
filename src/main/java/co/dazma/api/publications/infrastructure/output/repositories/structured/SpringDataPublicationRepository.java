package co.dazma.api.publications.infrastructure.output.repositories.structured;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataPublicationRepository extends JpaRepository<PublicationEntity, UUID> {
}
