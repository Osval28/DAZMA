package co.dazma.api.publications.services.input.ports;

import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.services.input.models.CreatePublicationCommand;

public interface CreatePublicationUseCase {

    Publication create(CreatePublicationCommand command);
}
