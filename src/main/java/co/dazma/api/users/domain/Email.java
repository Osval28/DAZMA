package co.dazma.api.users.domain;

import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern EMAIL_FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        if (value.isBlank()) {
            throw new InvalidUserException(UserErrorCode.EMAIL_REQUIRED);
        }
        if (!EMAIL_FORMAT.matcher(value).matches()) {
            throw new InvalidUserException(UserErrorCode.EMAIL_INVALID);
        }
    }

}
