package co.dazma.api.shared.infrastructure;

public interface DomainMapper <E, D> {
    D toDomain(E entity);
    E toEntity(D domain);



}
