package co.dazma.api.publications.services.output.ports;

import co.dazma.api.publications.domain.PublicationRules;

public interface PublicationRulesPort {
    PublicationRules current();
}
