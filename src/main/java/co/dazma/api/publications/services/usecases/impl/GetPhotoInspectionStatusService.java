package co.dazma.api.publications.services.usecases.impl;

import co.dazma.api.publications.domain.enums.PhotoInspectionStatus;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import co.dazma.api.publications.services.usecases.contract.GetPhotoInspectionStatusUseCase;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class GetPhotoInspectionStatusService implements GetPhotoInspectionStatusUseCase {
    private final OperatingOwnerResolver owners;
    private final PublicationPhotoStoragePort storage;

    public GetPhotoInspectionStatusService(OperatingOwnerResolver owners, PublicationPhotoStoragePort storage) {
        this.owners = owners;
        this.storage = storage;
    }

    @Override
    public PhotoInspectionStatus execute(String ownerExternalId, UUID photoId) {
        return storage.inspectionStatus(owners.resolveId(ownerExternalId), photoId);
    }
}
