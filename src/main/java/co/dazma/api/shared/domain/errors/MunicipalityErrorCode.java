package co.dazma.api.shared.domain.errors;

public enum MunicipalityErrorCode implements ErrorCode {

    MUNICIPALITY_REQUIRED("MUNICIPALITY_REQUIRED",
            "municipality.required"),
    MUNICIPALITY_INVALID_FORMAT("MUNICIPALITY_INVALID_FORMAT",
            "municipality.invalid-format"),
    MUNICIPALITY_NOT_FOUND("MUNICIPALITY_NOT_FOUND",
            "municipality.not-found");

    private final String code;
    private final String messageKey;

    MunicipalityErrorCode(String code, String messageKey) {
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
