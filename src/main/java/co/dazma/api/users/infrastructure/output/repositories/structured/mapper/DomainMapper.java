package co.dazma.api.users.infrastructure.output.repositories.structured.mapper;

public interface DomainMapper <E, D> {
    D toDomain(E entity);
    E toEntity(D domain);



}
