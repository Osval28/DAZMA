package co.dazma.api.publications.services.input.ports;

import co.dazma.api.publications.domain.enums.PhotoInspectionStatus;
import java.util.UUID;

public interface GetPhotoInspectionStatusInputPort {
    PhotoInspectionStatus execute(String ownerExternalId, UUID photoId);
}
