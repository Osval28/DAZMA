package co.dazma.api.publications.services.input.ports;

import co.dazma.api.publications.services.input.dto.PublicationRulesOutputDto;

public interface GetPublicationRulesInputPort {
    PublicationRulesOutputDto execute();
}
