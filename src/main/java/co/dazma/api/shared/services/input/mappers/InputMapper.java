package co.dazma.api.shared.services.input.mappers;

public interface InputMapper<I, D> {

    D toDomain(I input);
}
