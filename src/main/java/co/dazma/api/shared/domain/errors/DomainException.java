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
        if (arguments == null) {
            this.arguments = new Object[0];
        } else {
            this.arguments = arguments.clone();
        }
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
