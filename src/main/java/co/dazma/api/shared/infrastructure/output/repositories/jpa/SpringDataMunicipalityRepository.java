package co.dazma.api.shared.infrastructure.output.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataMunicipalityRepository extends JpaRepository<MunicipalityEntityJPA, String> {
}
