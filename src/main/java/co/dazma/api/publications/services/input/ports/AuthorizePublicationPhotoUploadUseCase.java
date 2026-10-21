package co.dazma.api.publications.services.input.ports;

import co.dazma.api.publications.services.input.models.AuthorizePublicationPhotoUploadCommand;
import co.dazma.api.publications.services.input.models.PublicationPhotoUploadAuthorization;

public interface AuthorizePublicationPhotoUploadUseCase {

    PublicationPhotoUploadAuthorization authorize(
            AuthorizePublicationPhotoUploadCommand command
    );
}
