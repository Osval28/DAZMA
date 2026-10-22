package co.dazma.api.security.domain;

import co.dazma.api.shared.domain.errors.ErrorCode;

public enum SecurityErrorCode implements ErrorCode {

    AUTHENTICATION_REQUIRED(
            "AUTHENTICATION_REQUIRED",
            "security.authentication.required"
    ),
    IDENTITY_TOKEN_INVALID(
            "IDENTITY_TOKEN_INVALID",
            "security.identity-token.invalid"
    ),
    ACCESS_DENIED(
            "ACCESS_DENIED",
            "security.access-denied"
    );

    private final String code;
    private final String messageKey;

    SecurityErrorCode(String code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String messageKey() {
        return messageKey;
    }
}
