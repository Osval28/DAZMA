package co.dazma.api.publications.infrastructure.output.parameters;

import co.dazma.api.publications.services.output.ports.PhotoDownloadParametersPort;
import co.dazma.api.shared.services.output.ports.ParameterCatalog;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public final class CatalogPhotoDownloadParametersAdapter implements PhotoDownloadParametersPort {

    private final ParameterCatalog catalog;

    public CatalogPhotoDownloadParametersAdapter(ParameterCatalog catalog) {
        this.catalog = catalog;
        downloadValidity();
    }

    @Override
    public Duration downloadValidity() {
        Duration validity = catalog.getDuration(PublicationParameter.PHOTO_DOWNLOAD_VALIDITY);
        if (validity.isZero() || validity.isNegative()) {
            throw new IllegalStateException("La vigencia de descarga debe ser mayor que cero");
        }
        return validity;
    }
}
