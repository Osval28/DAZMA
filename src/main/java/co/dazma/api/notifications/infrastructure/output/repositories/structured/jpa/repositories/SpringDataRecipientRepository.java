package co.dazma.api.notifications.infrastructure.output.repositories.structured.jpa.repositories;

import co.dazma.api.notifications.infrastructure.output.repositories.structured.jpa.entities.RecipientEntityJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataRecipientRepository extends JpaRepository<RecipientEntityJPA, UUID> {
}
