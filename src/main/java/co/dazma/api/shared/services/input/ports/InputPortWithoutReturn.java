package co.dazma.api.shared.services.input.ports;

public interface InputPortWithoutReturn<I> {

    void execute(I input);
}
