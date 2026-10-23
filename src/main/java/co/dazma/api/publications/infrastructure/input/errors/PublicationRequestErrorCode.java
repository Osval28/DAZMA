package co.dazma.api.publications.infrastructure.input.errors;

import co.dazma.api.shared.domain.errors.ErrorCode;

public enum PublicationRequestErrorCode implements ErrorCode {

    REQUEST_BODY_INVALID(
            "PUBLICATION_REQUEST_BODY_INVALID",
            "publication.request.body.invalid"
    );

    private final String code;
    private final String messageKey;

    PublicationRequestErrorCode(String code, String messageKey) {
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
