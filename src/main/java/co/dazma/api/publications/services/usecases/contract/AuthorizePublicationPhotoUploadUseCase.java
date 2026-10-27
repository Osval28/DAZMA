package co.dazma.api.publications.services.usecases.contract;

import co.dazma.api.publications.domain.PhotoUploadRequest;
import co.dazma.api.publications.services.usecases.models.PhotoUploadGrant;
import co.dazma.api.shared.services.usecases.UseCaseWithReturn;

public interface AuthorizePublicationPhotoUploadUseCase extends UseCaseWithReturn<PhotoUploadRequest, PhotoUploadGrant> {
}
