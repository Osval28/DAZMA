package co.dazma.api.publications.services.input.ports;

import co.dazma.api.publications.services.input.dto.CreatePublicationInputDto;
import co.dazma.api.publications.services.input.dto.CreatePublicationOutputDto;
import co.dazma.api.shared.services.input.ports.InputPortWithReturn;

public interface CreatePublicationInputPort
        extends InputPortWithReturn<CreatePublicationInputDto, CreatePublicationOutputDto> {
}
