package co.dazma.api.shared.domain.errors;

public abstract class DomainNotAllowedException extends DomainException {

    protected DomainNotAllowedException(ErrorCode errorCode, Object ... args) {
        super(errorCode, args);
    }
}
