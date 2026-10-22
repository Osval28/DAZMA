package co.dazma.api.publications.domain;

public record Description(String text) {

    private static final int MAX_LENGTH = 1000;

    public Description {
        text = text.trim();
        if (text.isEmpty()) {
            throw new InvalidPublicationException(PublicationErrorCode.DESCRIPTION_REQUIRED);
        }
        if (text.length() > MAX_LENGTH) {
            throw new InvalidPublicationException(PublicationErrorCode.DESCRIPTION_TOO_LONG, MAX_LENGTH);
        }
    }

}
