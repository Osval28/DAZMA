package co.dazma.api.publications.services.input.ports;

import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.services.input.dto.CreatePublicationInputDto;

public interface CreatePublicationUseCase {

    Publication create(CreatePublicationInputDto input);
}
