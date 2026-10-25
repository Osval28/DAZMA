package co.dazma.api.shared.services.output.ports;

import java.time.Duration;
import java.util.Set;

public interface ParameterCatalog {

    String getText(ParameterKey key);

    Set<String> getTextSet(ParameterKey key);

    long getBytes(ParameterKey key);

    Duration getDuration(ParameterKey key);
}
