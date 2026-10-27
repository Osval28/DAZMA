package co.dazma.api.publications.services.usecases.contract;

import co.dazma.api.publications.services.usecases.models.PublicationDetail;
import co.dazma.api.shared.services.usecases.UseCaseWithReturn;

import java.util.UUID;

public interface GetPublicationUseCase extends UseCaseWithReturn<UUID, PublicationDetail> {
}
