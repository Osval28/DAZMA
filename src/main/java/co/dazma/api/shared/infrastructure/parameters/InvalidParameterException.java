package co.dazma.api.shared.infrastructure.parameters;

import co.dazma.api.shared.domain.errors.TechnicalException;
import co.dazma.api.shared.services.output.ports.ParameterKey;

public class InvalidParameterException extends TechnicalException {

    public InvalidParameterException(ParameterErrorCode errorCode, ParameterKey parameter) {
        super(errorCode, null, parameter.key());
    }
}
