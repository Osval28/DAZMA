package co.dazma.api.publications.services.input.adapters;

import co.dazma.api.publications.services.input.adapters.mappers.PhotoUploadMapper;
import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadInputDto;
import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadOutputDto;
import co.dazma.api.publications.services.input.ports.AuthorizePublicationPhotoUploadInputPort;
import co.dazma.api.publications.services.usecases.contract.AuthorizePublicationPhotoUploadUseCase;
import co.dazma.api.publications.services.usecases.models.PhotoUploadGrant;
import org.springframework.stereotype.Service;

@Service
public class AuthorizePublicationPhotoUploadInputAdapter implements AuthorizePublicationPhotoUploadInputPort {

    private final AuthorizePublicationPhotoUploadUseCase useCase;
    private final PhotoUploadMapper mapper;

    public AuthorizePublicationPhotoUploadInputAdapter(
            AuthorizePublicationPhotoUploadUseCase useCase,
            PhotoUploadMapper mapper
    ) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @Override
    public AuthorizePublicationPhotoUploadOutputDto execute(AuthorizePublicationPhotoUploadInputDto input) {
        PhotoUploadGrant grant = useCase.execute(mapper.toDomain(input));
        return mapper.toOutput(grant);
    }
}
