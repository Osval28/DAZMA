package co.dazma.api.users.infrastructure.output.parameters;

import co.dazma.api.shared.services.output.ports.ParameterCatalog;
import co.dazma.api.users.domain.PhoneRules;
import co.dazma.api.users.services.output.ports.PhoneRulesPort;
import org.springframework.stereotype.Component;

@Component
public class CatalogPhoneRulesAdapter implements PhoneRulesPort {

    private final ParameterCatalog catalog;

    public CatalogPhoneRulesAdapter(ParameterCatalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public PhoneRules current() {
        return new PhoneRules(
                catalog.getText(UserParameter.PHONE_COUNTRY_CODE),
                catalog.getInt(UserParameter.PHONE_NATIONAL_LENGTH),
                catalog.getText(UserParameter.PHONE_MOBILE_PREFIX)
        );
    }
}
