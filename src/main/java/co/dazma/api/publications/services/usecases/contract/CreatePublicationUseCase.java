package co.dazma.api.publications.services.usecases.contract;

import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationDraft;
import co.dazma.api.shared.services.usecases.UseCaseWithReturn;

public interface CreatePublicationUseCase extends UseCaseWithReturn<PublicationDraft, Publication> {
}
