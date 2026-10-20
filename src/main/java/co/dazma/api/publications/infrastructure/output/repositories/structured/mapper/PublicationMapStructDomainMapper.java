package co.dazma.api.publications.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationPhoto;
import co.dazma.api.publications.infrastructure.output.repositories.structured.entities.PublicationEntity;
import co.dazma.api.shared.infrastructure.DomainMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PublicationMapStructDomainMapper extends DomainMapper<PublicationEntity, Publication> {
    @Override
    default Publication toDomain(PublicationEntity entity) {

        List<PublicationPhoto> photos = entity.photos().stream()
                .map(photoEntity -> new PublicationPhoto(photoEntity.id(), photoEntity.objectKey(), photoEntity.position()))
                .toList();


        return Publication.restore(
                entity.id(),
                entity.ownerId(),
                entity.name(),
                entity.type(),
                entity.description(),
                entity.city(),
                entity.status(),
                photos,
                entity.createdAt(),
                entity.price()
        );
    }

    @Override
    PublicationEntity toEntity(Publication domain);
}
