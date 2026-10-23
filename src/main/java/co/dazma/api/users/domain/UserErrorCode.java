package co.dazma.api.users.domain;

import co.dazma.api.shared.domain.errors.ErrorCode;

public enum UserErrorCode implements ErrorCode {

    REQUIRED_FIELD_MISSING(
            "USER_REQUIRED_FIELD_MISSING",
            "user.required-field.missing"
    ),
    PHONE_REQUIRED(
            "USER_PHONE_REQUIRED",
            "user.phone.required"
    ),
    NAME_REQUIRED(
            "USER_NAME_REQUIRED",
            "user.name.required"
    ),
    NAME_TOO_LONG(
            "USER_NAME_TOO_LONG",
            "user.name.too-long"
    ),
    PHONE_INVALID(
            "USER_PHONE_INVALID",
            "user.phone.invalid"
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
    ),
    NOT_FOUND(
            "USER_NOT_FOUND",
            "user.not-found"
    ),
    CANNOT_OPERATE(
            "USER_CANNOT_OPERATE",
            "user.cannot-operate"
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
