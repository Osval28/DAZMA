package co.dazma.api.publications.infrastructure.input.mapper;

import co.dazma.api.publications.domain.PublicationStatus;
import co.dazma.api.publications.domain.PublicationType;
import co.dazma.api.publications.infrastructure.input.dto.CreatePublicationResponseDto;
import co.dazma.api.publications.infrastructure.input.dto.GetPublicationResponseDto;
import co.dazma.api.publications.infrastructure.input.dto.PublicationOwnerResponseDto;
import co.dazma.api.publications.infrastructure.input.dto.PublicationStatusDto;
import co.dazma.api.publications.infrastructure.input.dto.PublicationTypeDto;
import co.dazma.api.publications.services.input.dto.CreatePublicationOutputDto;
import co.dazma.api.publications.services.input.dto.GetPublicationOutputDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PublicationWebMapper {

    PublicationType toDomain(PublicationTypeDto type);
    PublicationStatusDto toDto(PublicationStatus status);
    CreatePublicationResponseDto toResponse(CreatePublicationOutputDto output);

    @Mapping(target = "owner", source = "ownerId")
    GetPublicationResponseDto toResponse(GetPublicationOutputDto output);

    default PublicationOwnerResponseDto toOwner(UUID ownerId) {
        return ownerId == null ? null : new PublicationOwnerResponseDto(ownerId);
    }

}