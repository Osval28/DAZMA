package co.dazma.api.publications.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

import java.time.Duration;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "dazma.publications.photo-upload")
public final class PublicationPhotoUploadProperties {

    private final Set<String> allowedContentTypes;
    private final DataSize maximumSize;
    private final Duration validity;

    public PublicationPhotoUploadProperties(
            Set<String> allowedContentTypes,
            DataSize maximumSize,
            Duration validity
    ) {
        this.allowedContentTypes = normalizeContentTypes(allowedContentTypes);
        if (maximumSize == null || maximumSize.toBytes() <= 0) {
            throw new IllegalArgumentException("El tamaño máximo de las fotografías debe ser mayor que cero");
        }
        this.maximumSize = maximumSize;
        if (validity == null || validity.isZero() || validity.isNegative()) {
            throw new IllegalArgumentException("La vigencia de carga debe ser mayor que cero");
        }
        this.validity = validity;
    }

    public Set<String> allowedContentTypes() {
        return allowedContentTypes;
    }

    public long maximumContentLength() {
        return maximumSize.toBytes();
    }

    public Duration validity() {
        return validity;
    }

    private static Set<String> normalizeContentTypes(Set<String> contentTypes) {
        if (contentTypes == null || contentTypes.isEmpty()
                || contentTypes.stream().anyMatch(type -> type == null || type.isBlank())) {
            throw new IllegalArgumentException("Debe configurarse al menos un tipo de imagen");
        }

        return contentTypes.stream()
                .map(type -> type.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }
}
