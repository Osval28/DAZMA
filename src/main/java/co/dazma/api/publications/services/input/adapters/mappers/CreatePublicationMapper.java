package co.dazma.api.publications.services.input.adapters.mappers;

import co.dazma.api.publications.domain.Description;
import co.dazma.api.publications.domain.Name;
import co.dazma.api.publications.domain.Price;
import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationDraft;
import co.dazma.api.publications.services.input.dto.CreatePublicationInputDto;
import co.dazma.api.publications.services.input.dto.CreatePublicationOutputDto;
import co.dazma.api.shared.domain.Municipality;
import co.dazma.api.shared.services.input.mappers.InputMapper;
import co.dazma.api.shared.services.input.mappers.OutputMapper;
import org.springframework.stereotype.Component;

@Component
public class CreatePublicationMapper implements
        InputMapper<CreatePublicationInputDto, PublicationDraft>,
        OutputMapper<Publication, CreatePublicationOutputDto> {

    @Override
    public PublicationDraft toDomain(CreatePublicationInputDto input) {
        return new PublicationDraft(
                input.ownerExternalId(),
                input.ownerEmailVerified(),
                new Name(input.name()),
                input.type(),
                new Description(input.description()),
                new Municipality(input.municipality()),
                new Price(input.price()),
                input.photoIds()
        );
    }

    @Override
    public CreatePublicationOutputDto toOutput(Publication publication) {
        return new CreatePublicationOutputDto(
                publication.getId(),
                publication.getStatus(),
                publication.getCreatedAt()
        );
    }
}
