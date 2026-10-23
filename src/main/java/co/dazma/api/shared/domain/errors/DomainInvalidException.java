package co.dazma.api.shared.domain.errors;

public abstract class DomainInvalidException extends DomainException {

   protected DomainInvalidException(ErrorCode errorCode, Object ... args) {
        super(errorCode, args);
    }
}
