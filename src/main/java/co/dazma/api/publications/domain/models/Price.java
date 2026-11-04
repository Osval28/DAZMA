package co.dazma.api.publications.domain.models;

import co.dazma.api.publications.domain.exceptions.InvalidPublicationException;
import co.dazma.api.publications.domain.enums.PublicationErrorCode;

public record Price(Integer value) {

    public Price {
        valueMustBePositive(value);
    }

    private static void valueMustBePositive(Integer value) {
        if (value <= 0) {
            throw new InvalidPublicationException(PublicationErrorCode.VALUE_NOT_POSITIVE);
        }
    }
}
