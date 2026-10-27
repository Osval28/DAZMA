package co.dazma.api.shared.domain.errors;

public abstract class DomainException extends DazmaException {

    protected DomainException(ErrorCode errorCode, Object... arguments) {
        this(errorCode, null, arguments);
    }

    protected DomainException(ErrorCode errorCode, Throwable cause, Object... arguments) {
        super(errorCode, cause, arguments);
    }
}
