package co.dazma.api.publications.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.publications.domain.*;
import co.dazma.api.publications.infrastructure.output.repositories.structured.entities.PublicationEntity;
import co.dazma.api.publications.infrastructure.output.repositories.structured.entities.PublicationStatusEntity;
import co.dazma.api.publications.infrastructure.output.repositories.structured.entities.PublicationTypeEntity;
import co.dazma.api.shared.infrastructure.DomainMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import co.dazma.api.shared.domain.Municipality;
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
    PublicationEntity toEntity(Publication domain);

    PublicationType toDomain(PublicationTypeEntity type);

    PublicationStatus toDomain(PublicationStatusEntity status);

    default String map(Municipality municipality) {
        return municipality == null ? null : municipality.code();
    }

    default String map(Description description) {
        return description == null ? null : description.text();
    }

    default String map(Name name) {
        return name == null ? null : name.name();
    }

    default Integer map(Price price) {
        return price == null ? null : price.value();
    }
}
