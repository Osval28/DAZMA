package co.dazma.api.shared.domain.errors;

public abstract class DomainUnauthenticatedException extends DomainException {

    protected DomainUnauthenticatedException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    protected DomainUnauthenticatedException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode, cause, args);
    }
}
