package co.dazma.api.publications.services.input.ports;

import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadInputDto;
import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadOutputDto;

public interface AuthorizePublicationPhotoUploadUseCase {

    AuthorizePublicationPhotoUploadOutputDto authorize(
            AuthorizePublicationPhotoUploadInputDto input
    );
}
