package co.dazma.api.users.domain;

import java.util.regex.Pattern;

public record Phone(String number) {

    private static final String COUNTRY_CODE = "57";
    private static final int NATIONAL_LENGTH = 10;
    private static final Pattern FORMATTING_CHARACTERS = Pattern.compile("[\\s\\-().+]");
    private static final Pattern COLOMBIAN_MOBILE = Pattern.compile("3\\d{9}");

    public Phone {
        if (number.isBlank()) {
            throw new InvalidUserException(UserErrorCode.PHONE_REQUIRED);
        }

        number = removeCountryCode(removeFormatting(number));

        if (!COLOMBIAN_MOBILE.matcher(number).matches()) {
            throw new InvalidUserException(UserErrorCode.PHONE_INVALID);
        }
    }

    private static String removeFormatting(String rawNumber) {
        return FORMATTING_CHARACTERS.matcher(rawNumber).replaceAll("");
    }

    private static String removeCountryCode(String digits) {
        boolean hasCountryCode = digits.length() == COUNTRY_CODE.length() + NATIONAL_LENGTH
                && digits.startsWith(COUNTRY_CODE);

        return hasCountryCode ? digits.substring(COUNTRY_CODE.length()) : digits;
    }
}
