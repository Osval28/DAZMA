package co.dazma.api.publications.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.publications.domain.Municipality;
import co.dazma.api.publications.domain.Price;
import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.domain.PublicationPhoto;
import co.dazma.api.publications.infrastructure.output.repositories.structured.entities.PublicationEntity;
import co.dazma.api.shared.infrastructure.DomainMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
                new Municipality(entity.municipality()),
                entity.status(),
                photos,
                entity.createdAt(),
                new Price(entity.price())
        );
    }

    @Override
    PublicationEntity toEntity(Publication domain);

    default String map(Municipality municipality) {
        return municipality == null ? null : municipality.name();
    }

    default Integer map(Price price) {
        return price == null ? null : price.value();
    }
}
