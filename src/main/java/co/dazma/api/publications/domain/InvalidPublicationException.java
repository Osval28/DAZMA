package co.dazma.api.publications.domain;

public class InvalidPublicationException extends IllegalArgumentException {

    public InvalidPublicationException(String message) {
        super(message);
    }
}
