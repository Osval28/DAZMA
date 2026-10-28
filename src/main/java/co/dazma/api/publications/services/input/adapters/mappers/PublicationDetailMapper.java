package co.dazma.api.publications.services.input.adapters.mappers;

import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.services.input.dto.GetPublicationOutputDto;
import co.dazma.api.publications.services.input.dto.GetPublicationPhotoOutputDto;
import co.dazma.api.publications.services.usecases.models.PhotoAccess;
import co.dazma.api.publications.services.usecases.models.PublicationDetail;
import co.dazma.api.shared.services.input.mappers.OutputMapper;
import org.springframework.stereotype.Component;

@Component
public class PublicationDetailMapper implements OutputMapper<PublicationDetail, GetPublicationOutputDto> {

    @Override
    public GetPublicationOutputDto toOutput(PublicationDetail detail) {
        Publication publication = detail.publication();
        return new GetPublicationOutputDto(
                publication.getId(),
                publication.getOwnerId(),
                publication.getName().name(),
                publication.getType(),
                publication.getDescription().text(),
                publication.getMunicipality().name(),
                publication.getStatus(),
                publication.getCreatedAt(),
                publication.getPrice().value(),
                detail.photos().stream()
                        .map(PublicationDetailMapper::toPhotoOutput)
                        .toList()
        );
    }

    private static GetPublicationPhotoOutputDto toPhotoOutput(PhotoAccess photo) {
        return new GetPublicationPhotoOutputDto(photo.photoId(), photo.url(), photo.position());
    }
}
