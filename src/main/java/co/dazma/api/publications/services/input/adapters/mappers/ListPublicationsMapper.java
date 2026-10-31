package co.dazma.api.publications.services.input.adapters.mappers;

import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.services.input.dto.ListPublicationsInputDto;
import co.dazma.api.publications.services.input.dto.ListPublicationsOutputDto;
import co.dazma.api.publications.services.input.dto.PublicationSummaryOutputDto;
import co.dazma.api.publications.services.usecases.models.PublicationSearchCriteria;
import co.dazma.api.publications.services.usecases.models.PublicationSummary;
import co.dazma.api.publications.services.usecases.models.PublicationSummaryPage;
import co.dazma.api.shared.domain.Municipality;
import co.dazma.api.shared.services.input.mappers.InputMapper;
import co.dazma.api.shared.services.input.mappers.OutputMapper;
import org.springframework.stereotype.Component;

@Component
public class ListPublicationsMapper implements
        InputMapper<ListPublicationsInputDto, PublicationSearchCriteria>,
        OutputMapper<PublicationSummaryPage, ListPublicationsOutputDto> {

    @Override
    public PublicationSearchCriteria toDomain(ListPublicationsInputDto input) {
        return new PublicationSearchCriteria(
                input.municipality()
                        .filter(code -> !code.isBlank())
                        .map(Municipality::new),
                input.page(),
                input.size()
        );
    }

    @Override
    public ListPublicationsOutputDto toOutput(PublicationSummaryPage page) {
        return new ListPublicationsOutputDto(
                page.items().stream().map(ListPublicationsMapper::toSummary).toList(),
                page.page(),
                page.size(),
                page.hasNext()
        );
    }

    private static PublicationSummaryOutputDto toSummary(PublicationSummary summary) {
        Publication publication = summary.publication();
        return new PublicationSummaryOutputDto(
                publication.getId(),
                publication.getName().name(),
                publication.getType(),
                publication.getMunicipality().code(),
                publication.getPrice().value(),
                summary.cover().url(),
                publication.getCreatedAt()
        );
    }
}
