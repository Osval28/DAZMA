package co.dazma.api.users.domain;

public record Name(String value) {

    private static final int MAX_LENGTH = 255;

    public Name {
        value = value.trim();
        if (value.isEmpty()) {
            throw new InvalidUserException(UserErrorCode.NAME_REQUIRED);
        }
        if (value.codePointCount(0, value.length()) > MAX_LENGTH) {
            throw new InvalidUserException(UserErrorCode.NAME_TOO_LONG, MAX_LENGTH);
        }
    }

}
