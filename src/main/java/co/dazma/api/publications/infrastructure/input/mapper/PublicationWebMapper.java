package co.dazma.api.publications.infrastructure.input.mapper;

import co.dazma.api.publications.domain.PublicationStatus;
import co.dazma.api.publications.domain.PublicationType;
import co.dazma.api.publications.infrastructure.input.dto.CreatePublicationResponseDto;
import co.dazma.api.publications.infrastructure.input.dto.PublicationStatusDto;
import co.dazma.api.publications.infrastructure.input.dto.PublicationTypeDto;
import co.dazma.api.publications.services.input.dto.CreatePublicationOutputDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PublicationWebMapper {

    PublicationType toDomain(PublicationTypeDto type);
    PublicationStatusDto toDto(PublicationStatus status);
    CreatePublicationResponseDto toResponse(CreatePublicationOutputDto output);

}