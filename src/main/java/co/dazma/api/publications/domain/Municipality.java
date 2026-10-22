package co.dazma.api.publications.domain;

public record Municipality(String name) {
    private static final int MAX_LENGTH = 255;

    public Municipality {
        name = name.trim();
        if (name.codePointCount(0, name.length()) > MAX_LENGTH) {
            throw new InvalidPublicationException(PublicationErrorCode.MUNICIPALITY_TOO_LONG,MAX_LENGTH);
        }
        if (name.isBlank()) {
            throw new InvalidPublicationException(PublicationErrorCode.MUNICIPALITY_REQUIRED);
        }

    }

}
