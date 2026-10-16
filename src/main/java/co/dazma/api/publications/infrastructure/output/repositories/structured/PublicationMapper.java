package co.dazma.api.publications.infrastructure.output.repositories.structured;

import co.dazma.api.publications.domain.Publication;

public final class PublicationMapper {

    private PublicationMapper() {
    }

    public static PublicationEntity toEntity(Publication publication) {
        return new PublicationEntity(
                publication.getId(),
                publication.getOwnerId(),
                publication.getName(),
                publication.getType(),
                publication.getDescription(),
                publication.getCity(),
                publication.getStatus(),
                publication.getCreatedAt()
        );
    }
}
