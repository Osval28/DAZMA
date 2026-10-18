package co.dazma.api.users.infrastructure.output.repositories.structured.jpa.repositories;

import co.dazma.api.users.infrastructure.output.repositories.structured.jpa.entities.UserEntityJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserRepository extends JpaRepository<UserEntityJPA, UUID> {

    Optional<UserEntityJPA> findByExternalId(String externalId);

}