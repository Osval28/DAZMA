package co.dazma.api.shared.services.input.mappers;

public interface OutputMapper<D, O> {

    O toOutput(D domain);
}
