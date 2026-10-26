package co.dazma.api.publications.services.input.ports;

import co.dazma.api.publications.services.input.dto.GetPublicationOutputDto;

import java.util.UUID;

public interface GetPublicationUseCase {

    GetPublicationOutputDto getById(UUID publicationId);
}
