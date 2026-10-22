package co.dazma.api.publications.domain;

public record Municipality(String name) {

    public Municipality {
        if (name.length() > 255) {
            throw new InvalidPublicationException(PublicationErrorCode.MUNICIPALITY_TOO_LONG);
        }
        if (name.isBlank()) {
            throw new InvalidPublicationException(PublicationErrorCode.MUNICIPALITY_REQUIRED);
        }
        name = name.trim();
    }

}
