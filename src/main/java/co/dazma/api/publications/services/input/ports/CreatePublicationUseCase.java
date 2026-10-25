package co.dazma.api.publications.services.input.ports;

import co.dazma.api.publications.services.input.dto.CreatePublicationInputDto;
import co.dazma.api.publications.services.input.dto.CreatePublicationOutputDto;

public interface CreatePublicationUseCase {

    CreatePublicationOutputDto create(CreatePublicationInputDto input);
}
