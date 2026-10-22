package co.dazma.api.publications.domain;

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
