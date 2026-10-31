package co.dazma.api.publications.services.input.ports;

import co.dazma.api.publications.services.input.dto.ListPublicationsInputDto;
import co.dazma.api.publications.services.input.dto.ListPublicationsOutputDto;
import co.dazma.api.shared.services.input.ports.InputPortWithReturn;

public interface ListPublicationsInputPort extends InputPortWithReturn<ListPublicationsInputDto, ListPublicationsOutputDto> {
}
