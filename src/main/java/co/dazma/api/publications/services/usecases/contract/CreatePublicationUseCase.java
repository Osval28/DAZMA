package co.dazma.api.publications.services.usecases.contract;

import co.dazma.api.publications.domain.models.Publication;
import co.dazma.api.publications.domain.models.PublicationDraft;
import co.dazma.api.shared.services.usecases.UseCaseWithReturn;

public interface CreatePublicationUseCase extends UseCaseWithReturn<PublicationDraft, Publication> {
}
