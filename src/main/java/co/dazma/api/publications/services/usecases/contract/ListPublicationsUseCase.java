package co.dazma.api.publications.services.usecases.contract;

import co.dazma.api.publications.services.usecases.models.PublicationSearchCriteria;
import co.dazma.api.publications.services.usecases.models.PublicationSummaryPage;
import co.dazma.api.shared.services.usecases.UseCaseWithReturn;

public interface ListPublicationsUseCase extends UseCaseWithReturn<PublicationSearchCriteria, PublicationSummaryPage> {
}
