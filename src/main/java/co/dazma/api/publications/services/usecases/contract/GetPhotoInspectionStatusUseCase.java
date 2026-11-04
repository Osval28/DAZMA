package co.dazma.api.publications.services.usecases.contract;

import co.dazma.api.publications.domain.enums.PhotoInspectionStatus;
import java.util.UUID;

public interface GetPhotoInspectionStatusUseCase {
    PhotoInspectionStatus execute(String ownerExternalId, UUID photoId);
}
