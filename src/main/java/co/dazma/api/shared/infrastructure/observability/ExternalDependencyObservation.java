package co.dazma.api.shared.infrastructure.observability;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Supplier;

@Component
public class ExternalDependencyObservation {

    private static final String OBSERVATION_NAME = "dazma.external.request";

    private final ObservationRegistry registry;

    public ExternalDependencyObservation(ObservationRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "El registro de observaciones es obligatorio");
    }

    public <T> T observe(String dependency, String operation, Supplier<T> action) {
        return observation(dependency, operation).observe(action);
    }

    public void observe(String dependency, String operation, Runnable action) {
        observation(dependency, operation).observe(action);
    }

    public <T, E extends Throwable> T observeChecked(
            String dependency,
            String operation,
            Observation.CheckedCallable<T, E> action
    ) throws E {
        return observation(dependency, operation).observeChecked(action);
    }

    public <E extends Throwable> void observeChecked(
            String dependency,
            String operation,
            Observation.CheckedRunnable<E> action
    ) throws E {
        observation(dependency, operation).observeChecked(action);
    }

    private Observation observation(String dependency, String operation) {
        String normalizedDependency = requireText(dependency, "La dependencia es obligatoria");
        String normalizedOperation = requireText(operation, "La operación es obligatoria");
        return Observation.createNotStarted(OBSERVATION_NAME, registry)
                .contextualName(normalizedDependency + " " + normalizedOperation)
                .lowCardinalityKeyValue("dependency", normalizedDependency)
                .lowCardinalityKeyValue("operation", normalizedOperation);
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
