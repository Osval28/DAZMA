package co.dazma.api.shared.domain;

import co.dazma.api.shared.domain.errors.InvalidMunicipalityException;
import co.dazma.api.shared.domain.errors.MunicipalityErrorCode;

public record Municipality(String name) {
    private static final int MAX_LENGTH = 255;

    public Municipality {
        name = name.trim();
        if (name.isBlank()) {
            throw new InvalidMunicipalityException(MunicipalityErrorCode.MUNICIPALITY_REQUIRED);
        }
        if (name.codePointCount(0, name.length()) > MAX_LENGTH) {
            throw new InvalidMunicipalityException(MunicipalityErrorCode.MUNICIPALITY_TOO_LONG,MAX_LENGTH);
        }
    }

}
