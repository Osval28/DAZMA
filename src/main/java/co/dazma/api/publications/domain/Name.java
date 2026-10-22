package co.dazma.api.publications.domain;

public record Name(String name) {

    private static final int MAX_LENGTH = 255;

    public Name {
        name = name.trim();
        if (name.isBlank()) {
            throw new InvalidPublicationException(PublicationErrorCode.NAME_REQUIRED);
        }
        if (name.length() > MAX_LENGTH){
            throw new InvalidPublicationException(PublicationErrorCode.NAME_TOO_LONG,MAX_LENGTH);
        }
    }

}
