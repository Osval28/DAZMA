package co.dazma.api.publications.services.input.adapters;

import co.dazma.api.publications.services.input.ports.GetPhotoInspectionStatusInputPort;
import co.dazma.api.publications.domain.enums.PhotoInspectionStatus;
import co.dazma.api.publications.services.usecases.contract.GetPhotoInspectionStatusUseCase;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class GetPhotoInspectionStatusInputAdapter implements GetPhotoInspectionStatusInputPort {
    private final GetPhotoInspectionStatusUseCase useCase;

    public GetPhotoInspectionStatusInputAdapter(GetPhotoInspectionStatusUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public PhotoInspectionStatus execute(String ownerExternalId, UUID photoId) {
        return useCase.execute(ownerExternalId, photoId);
    }
}
