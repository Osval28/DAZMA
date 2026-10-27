package co.dazma.api.shared.domain.errors;

public abstract class TechnicalException extends DazmaException {

    protected TechnicalException(ErrorCode errorCode, Throwable cause, Object... arguments) {
        super(errorCode, cause, arguments);
    }
}
