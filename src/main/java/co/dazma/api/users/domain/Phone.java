package co.dazma.api.users.domain;

import java.util.regex.Pattern;

public record Phone(String number) {

    private static final Pattern FORMATTING_CHARACTERS = Pattern.compile("[\\s\\-().+]");
    private static final Pattern DIGITS = Pattern.compile("[0-9]+");

    public Phone {
        if (number.isBlank()) {
            throw new InvalidUserException(UserErrorCode.PHONE_REQUIRED);
        }

        number = removeFormatting(number);

        if (!DIGITS.matcher(number).matches()) {
            throw new InvalidUserException(UserErrorCode.PHONE_INVALID);
        }
    }

    private static String removeFormatting(String rawNumber) {
        return FORMATTING_CHARACTERS.matcher(rawNumber).replaceAll("");
    }

    public static Phone fromInput(String rawNumber, PhoneRules rules) {
        if (rawNumber == null || rawNumber.isBlank()) {
            throw new InvalidUserException(UserErrorCode.PHONE_REQUIRED);
        }
        String digits = removeFormatting(rawNumber);
        boolean hasCountryCode = digits.length() == rules.countryCode().length() + rules.nationalLength()
                && digits.startsWith(rules.countryCode());

        String national = hasCountryCode ? digits.substring(rules.countryCode().length()) : digits;
        if (national.length() != rules.nationalLength() || !national.startsWith(rules.mobilePrefix())) {
            throw new InvalidUserException(UserErrorCode.PHONE_INVALID);
        }

        return new Phone(national);
    }
}
