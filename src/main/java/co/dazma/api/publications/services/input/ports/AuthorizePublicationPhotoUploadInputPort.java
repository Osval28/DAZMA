package co.dazma.api.publications.services.input.ports;

import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadInputDto;
import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadOutputDto;
import co.dazma.api.shared.services.input.ports.InputPortWithReturn;

public interface AuthorizePublicationPhotoUploadInputPort
        extends InputPortWithReturn<AuthorizePublicationPhotoUploadInputDto, AuthorizePublicationPhotoUploadOutputDto> {
}
