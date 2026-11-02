package co.dazma.api.shared.infrastructure.parameters;

import co.dazma.api.shared.domain.errors.ErrorCode;

public enum ParameterErrorCode implements ErrorCode {

    VALUES_REQUIRED(
            "PARAMETER_VALUES_REQUIRED",
            "parameter.values.required"
    ),
    VALUE_NOT_POSITIVE(
            "PARAMETER_VALUE_NOT_POSITIVE",
            "parameter.value.not-positive"
    );

    private final String code;
    private final String messageKey;

    ParameterErrorCode(String code, String messageKey) {
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
