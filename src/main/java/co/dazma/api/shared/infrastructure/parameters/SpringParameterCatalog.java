package co.dazma.api.shared.infrastructure.parameters;

import co.dazma.api.shared.services.output.ports.ParameterCatalog;
import co.dazma.api.shared.services.output.ports.ParameterKey;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SpringParameterCatalog implements ParameterCatalog {

    private final Environment environment;

    public SpringParameterCatalog(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String getText(ParameterKey key) {
        return environment.getRequiredProperty(key.key());
    }

    @Override
    public Set<String> getTextSet(ParameterKey key) {
        return Arrays.stream(environment.getRequiredProperty(key.key(), String[].class))
                .map(String::trim)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public long getBytes(ParameterKey key) {
        return environment.getRequiredProperty(key.key(), DataSize.class).toBytes();
    }

    @Override
    public Duration getDuration(ParameterKey key) {
        return environment.getRequiredProperty(key.key(), Duration.class);
    }
}
