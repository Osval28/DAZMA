package co.dazma.api.shared.infrastructure;

public interface JpaMapper <J, E> {
    J toJpaEntity(E entity);
    E toEntity(J jpaEntity);
}
