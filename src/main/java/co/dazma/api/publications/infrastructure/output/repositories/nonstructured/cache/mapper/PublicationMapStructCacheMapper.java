package co.dazma.api.publications.infrastructure.output.repositories.nonstructured.cache.mapper;

import co.dazma.api.publications.domain.enums.PublicationStatus;
import co.dazma.api.publications.domain.enums.PublicationType;
import co.dazma.api.publications.domain.models.*;
import co.dazma.api.publications.infrastructure.output.repositories.nonstructured.cache.entities.PublicationCacheEntity;
import co.dazma.api.publications.infrastructure.output.repositories.nonstructured.cache.entities.PublicationStatusCacheEntity;
import co.dazma.api.publications.infrastructure.output.repositories.nonstructured.cache.entities.PublicationTypeCacheEntity;
import co.dazma.api.shared.domain.models.Municipality;
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
                entity.ownerName(),
                new Name(entity.name()),
                toDomain(entity.type()),
                new Description(entity.description()),
                new Municipality(entity.municipality()),
                toDomain(entity.status()),
                photos,
                entity.createdAt(),
                new Price(entity.price())
        );
    }

    @Override
    PublicationCacheEntity toEntity(Publication domain);

    PublicationType toDomain(PublicationTypeCacheEntity type);

    PublicationStatus toDomain(PublicationStatusCacheEntity status);

    default String map(Municipality municipality) {
        return municipality.code();
    }

    default String map(Description description) {
        return description.text();
    }

    default String map(Name name) {
        return name.name();
    }

    default Integer map(Price price) {
        return price.value();
    }
}
