package co.dazma.api.publications.services.input.adapters;

import co.dazma.api.publications.services.input.adapters.mappers.PublicationDetailMapper;
import co.dazma.api.publications.services.input.dto.GetPublicationOutputDto;
import co.dazma.api.publications.services.input.ports.GetPublicationInputPort;
import co.dazma.api.publications.services.usecases.contract.GetPublicationUseCase;
import co.dazma.api.publications.services.usecases.models.PublicationDetail;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetPublicationInputAdapter implements GetPublicationInputPort {

    private final GetPublicationUseCase useCase;
    private final PublicationDetailMapper mapper;

    public GetPublicationInputAdapter(GetPublicationUseCase useCase, PublicationDetailMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @Override
    public GetPublicationOutputDto execute(UUID publicationId) {
        PublicationDetail detail = useCase.execute(publicationId);
        return mapper.toOutput(detail);
    }
}
