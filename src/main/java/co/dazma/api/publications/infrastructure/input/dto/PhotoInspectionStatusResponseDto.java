package co.dazma.api.publications.infrastructure.input.dto;

import co.dazma.api.publications.domain.enums.PhotoInspectionStatus;

public record PhotoInspectionStatusResponseDto(PhotoInspectionStatus status) {
}
