package co.dazma.api.publications.infrastructure.output.parameters;

import co.dazma.api.publications.services.output.ports.PhotoUploadParametersPort;
import co.dazma.api.shared.infrastructure.parameters.InvalidParameterException;
import co.dazma.api.shared.infrastructure.parameters.ParameterErrorCode;
import co.dazma.api.shared.services.output.ports.ParameterCatalog;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public final class CatalogPhotoUploadParametersAdapter implements PhotoUploadParametersPort {

    private final ParameterCatalog catalog;

    public CatalogPhotoUploadParametersAdapter(ParameterCatalog catalog) {
        this.catalog = catalog;
        allowedContentTypes();
        maximumContentLength();
        uploadValidity();
    }

    @Override
    public Set<String> allowedContentTypes() {
        Set<String> contentTypes = catalog.getTextSet(PublicationParameter.PHOTO_ALLOWED_CONTENT_TYPES);
        if (contentTypes.isEmpty() || contentTypes.stream().anyMatch(String::isBlank)) {
            throw new InvalidParameterException(
                    ParameterErrorCode.VALUES_REQUIRED,
                    PublicationParameter.PHOTO_ALLOWED_CONTENT_TYPES
            );
        }
        return contentTypes.stream()
                .map(type -> type.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public long maximumContentLength() {
        long maximumContentLength = catalog.getBytes(PublicationParameter.PHOTO_MAXIMUM_SIZE);
        if (maximumContentLength <= 0) {
            throw new InvalidParameterException(
                    ParameterErrorCode.VALUE_NOT_POSITIVE,
                    PublicationParameter.PHOTO_MAXIMUM_SIZE
            );
        }
        return maximumContentLength;
    }

    @Override
    public Duration uploadValidity() {
        Duration validity = catalog.getDuration(PublicationParameter.PHOTO_UPLOAD_VALIDITY);
        if (validity.isZero() || validity.isNegative()) {
            throw new InvalidParameterException(
                    ParameterErrorCode.VALUE_NOT_POSITIVE,
                    PublicationParameter.PHOTO_UPLOAD_VALIDITY
            );
        }
        return validity;
    }
}
