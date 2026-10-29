package co.dazma.api.shared.domain;

import co.dazma.api.shared.domain.errors.InvalidMunicipalityException;
import co.dazma.api.shared.domain.errors.MunicipalityErrorCode;

import java.util.regex.Pattern;

/**
 * Municipio identificado por su código DIVIPOLA del DANE (5 dígitos: 2 del departamento
 * y 3 del municipio, por ejemplo {@code 05615} para Rionegro, Antioquia).
 * Que el código exista en el catálogo lo verifica el caso de uso, no este objeto de valor.
 */
public record Municipality(String code) {

    private static final Pattern DIVIPOLA_FORMAT = Pattern.compile("\\d{5}");

    public Municipality {
        code = code.trim();
        if (code.isBlank()) {
            throw new InvalidMunicipalityException(MunicipalityErrorCode.MUNICIPALITY_REQUIRED);
        }
        if (!DIVIPOLA_FORMAT.matcher(code).matches()) {
            throw new InvalidMunicipalityException(MunicipalityErrorCode.MUNICIPALITY_INVALID_FORMAT);
        }
    }

}
