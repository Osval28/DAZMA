package co.dazma.api.publications.services.input.ports;

import co.dazma.api.publications.services.input.dto.GetPublicationOutputDto;
import co.dazma.api.shared.services.input.ports.InputPortWithReturn;

import java.util.UUID;

public interface GetPublicationInputPort extends InputPortWithReturn<UUID, GetPublicationOutputDto> {
}
