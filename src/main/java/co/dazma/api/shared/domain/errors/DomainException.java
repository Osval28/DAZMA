package co.dazma.api.shared.domain.errors;

import java.util.Objects;

public abstract class DomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] arguments;

    protected DomainException(ErrorCode errorCode, Object... arguments) {
        this(errorCode, null, arguments);
    }

    protected DomainException(ErrorCode errorCode, Throwable cause, Object... arguments) {
        super(
                Objects.requireNonNull(errorCode, "El código del error es obligatorio").messageKey(),
                cause
        );

        this.errorCode = errorCode;
        this.arguments = arguments == null
                ? new Object[0]
                : arguments.clone();
    }

    public String code() {
        return errorCode.code();
    }

    public ErrorCode errorCode() {
        return errorCode;
    }

    public String messageKey() {
        return errorCode.messageKey();
    }

    public Object[] arguments() {
        return arguments.clone();
    }
}
