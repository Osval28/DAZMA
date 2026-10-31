package co.dazma.api.shared.infrastructure.input.dto;

import co.dazma.api.shared.services.input.dto.MunicipalityOutputDto;

public record MunicipalityResponseDto(String code, String name, String department) {

    public static MunicipalityResponseDto from(MunicipalityOutputDto output) {
        return new MunicipalityResponseDto(output.code(), output.name(), output.department());
    }
}
