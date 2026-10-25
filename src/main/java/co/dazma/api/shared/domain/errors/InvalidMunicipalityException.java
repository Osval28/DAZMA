package co.dazma.api.shared.domain.errors;

public class InvalidMunicipalityException extends DomainInvalidException {

    public InvalidMunicipalityException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}
