package co.dazma.api.shared.services.usecases;

public interface UseCaseWithoutReturn<D> {

    void execute(D domain);
}
