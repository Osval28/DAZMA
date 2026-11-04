package co.dazma.api.publications.services.input.adapters.mappers;

import co.dazma.api.publications.domain.models.PublicationOwner;
import co.dazma.api.publications.domain.enums.PublicationOwnerState;
import co.dazma.api.publications.services.input.dto.RegisterPublicationOwnerInputDto;
import co.dazma.api.shared.services.input.mappers.InputMapper;
import org.springframework.stereotype.Component;

@Component
public class PublicationOwnerMapper implements InputMapper<RegisterPublicationOwnerInputDto, PublicationOwner> {

    @Override
    public PublicationOwner toDomain(RegisterPublicationOwnerInputDto input) {
        return new PublicationOwner(
                input.userId(),
                input.userName(),
                input.externalId(),
                PublicationOwnerState.valueOf(input.state())
        );
    }
}
