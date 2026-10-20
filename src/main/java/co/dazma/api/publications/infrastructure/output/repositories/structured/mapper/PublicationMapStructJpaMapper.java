package co.dazma.api.publications.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.publications.infrastructure.output.repositories.structured.entities.PublicationEntity;
import co.dazma.api.publications.infrastructure.output.repositories.structured.entities.PublicationPhotoEntity;
import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.entities.PublicationEntityJPA;
import co.dazma.api.publications.infrastructure.output.repositories.structured.jpa.entities.PublicationPhotoEntityJPA;
import co.dazma.api.shared.infrastructure.JpaMapper;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PublicationMapStructJpaMapper extends JpaMapper<PublicationEntityJPA, PublicationEntity> {

    @Override
    PublicationEntity toEntity(PublicationEntityJPA jpaEntity);

    @Mapping(target = "photos", ignore = true)
    PublicationEntityJPA toJpaEntity(PublicationEntity entity);

    @AfterMapping
    default void addPhotos(PublicationEntity source, @MappingTarget PublicationEntityJPA target) {
        source.photos().forEach(p -> target.addPhoto(toPhotoJpa(p)));
    }

    PublicationPhotoEntityJPA toPhotoJpa(PublicationPhotoEntity photo);
}
