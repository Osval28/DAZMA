package co.dazma.api.shared.domain.errors;

public abstract class DomainNotFoundException extends DomainException {

    protected DomainNotFoundException(ErrorCode errorCode, Object ... args) {
        super(errorCode, args);
    }

}
