package co.dazma.api.publications.services.input.adapters.mappers;

import co.dazma.api.publications.domain.PhotoUploadRequest;
import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadInputDto;
import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadOutputDto;
import co.dazma.api.publications.services.usecases.models.PhotoUploadGrant;
import co.dazma.api.shared.services.input.mappers.InputMapper;
import co.dazma.api.shared.services.input.mappers.OutputMapper;
import org.springframework.stereotype.Component;

@Component
public class PhotoUploadMapper implements
        InputMapper<AuthorizePublicationPhotoUploadInputDto, PhotoUploadRequest>,
        OutputMapper<PhotoUploadGrant, AuthorizePublicationPhotoUploadOutputDto> {

    @Override
    public PhotoUploadRequest toDomain(AuthorizePublicationPhotoUploadInputDto input) {
        return new PhotoUploadRequest(input.ownerExternalId(), input.contentType(), input.contentLength());
    }

    @Override
    public AuthorizePublicationPhotoUploadOutputDto toOutput(PhotoUploadGrant grant) {
        return new AuthorizePublicationPhotoUploadOutputDto(grant.photoId(), grant.uploadUrl(), grant.expiresAt());
    }
}
