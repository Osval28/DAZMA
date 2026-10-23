package co.dazma.api.shared.domain.errors;

public abstract class DomainConflictException extends DomainException {

    protected DomainConflictException(ErrorCode errorCode, Object ... args) {
        super(errorCode, args);
    }
}
