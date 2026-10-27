package co.dazma.api.shared.services.usecases;

public interface UseCaseWithReturn<D, R> {

    R execute(D domain);
}
