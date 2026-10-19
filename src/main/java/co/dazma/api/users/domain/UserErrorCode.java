package co.dazma.api.users.domain;

import co.dazma.api.shared.domain.errors.ErrorCode;

public enum UserErrorCode implements ErrorCode {

    REQUIRED_FIELD_MISSING(
            "USER_REQUIRED_FIELD_MISSING",
            "user.required-field.missing"
    ),
    ALREADY_REGISTERED(
            "USER_ALREADY_REGISTERED",
            "user.already-registered"
    ),
    ALREADY_SUSPENDED(
            "USER_ALREADY_SUSPENDED",
            "user.already-suspended"
    ),
    NOT_SUSPENDED(
            "USER_NOT_SUSPENDED",
            "user.not-suspended"
    );

    private final String code;
    private final String messageKey;

    UserErrorCode(String code, String messageKey) {
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
