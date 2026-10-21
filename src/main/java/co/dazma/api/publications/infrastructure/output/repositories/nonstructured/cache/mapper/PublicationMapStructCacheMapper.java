package co.dazma.api.publications.infrastructure.output.repositories.nonstructured.cache.mapper;

import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationPhoto;
import co.dazma.api.publications.infrastructure.output.repositories.nonstructured.cache.entities.PublicationCacheEntity;
import co.dazma.api.shared.infrastructure.DomainMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PublicationMapStructCacheMapper extends DomainMapper<PublicationCacheEntity, Publication> {

    @Override
    default Publication toDomain(PublicationCacheEntity entity) {
        List<PublicationPhoto> photos = entity.photos().stream()
                .map(photo -> new PublicationPhoto(photo.id(), photo.objectKey(), photo.position()))
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
    PublicationCacheEntity toEntity(Publication domain);
}