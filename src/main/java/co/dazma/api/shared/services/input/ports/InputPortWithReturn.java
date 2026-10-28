package co.dazma.api.shared.services.input.ports;

public interface InputPortWithReturn<I, O> {

    O execute(I input);
}
