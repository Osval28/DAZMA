package co.dazma.api.users.infrastructure.output.repositories.structured.mapper;

public interface JpaMapper <J, E> {
    J toJpaEntity(E entity);
    E toEntity(J jpaEntity);
}
