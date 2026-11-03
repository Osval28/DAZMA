package co.dazma.api.publications.infrastructure.output.parameters;

import co.dazma.api.publications.domain.PublicationRules;
import co.dazma.api.publications.services.output.ports.PublicationRulesPort;
import co.dazma.api.shared.services.output.ports.ParameterCatalog;
import org.springframework.stereotype.Component;

@Component
public class CatalogPublicationRulesAdapter implements PublicationRulesPort {

    private final ParameterCatalog catalog;

    public CatalogPublicationRulesAdapter(ParameterCatalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public PublicationRules current() {
        return new PublicationRules(
                catalog.getInt(PublicationParameter.MAXIMUM_PHOTOS),
                catalog.getInt(PublicationParameter.MAXIMUM_PAGE_SIZE),
                catalog.getInt(PublicationParameter.DEFAULT_PAGE_SIZE)
        );
    }
}
