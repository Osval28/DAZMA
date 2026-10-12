package co.dazma.api.users.infrastructure.output.repositories.structured;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserRepository
        extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByExternalId(String externalId);
}