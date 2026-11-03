package co.dazma.api.publications.domain.models;

import co.dazma.api.publications.domain.exceptions.InvalidPublicationException;
import co.dazma.api.publications.domain.enums.PublicationErrorCode;

public record Description(String text) {

    private static final int MAX_LENGTH = 1000;

    public Description {
        text = text.trim();
        if (text.isEmpty()) {
            throw new InvalidPublicationException(PublicationErrorCode.DESCRIPTION_REQUIRED);
        }
        if (text.codePointCount(0, text.length()) > MAX_LENGTH) {
            throw new InvalidPublicationException(PublicationErrorCode.DESCRIPTION_TOO_LONG, MAX_LENGTH);
        }
    }

}
