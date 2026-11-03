package co.dazma.api.users.domain;

public record PhoneRules(String countryCode, int nationalLength, String mobilePrefix) {

    public PhoneRules {
        if (countryCode == null || !countryCode.matches("[1-9][0-9]{0,2}")
                || nationalLength < 1 || nationalLength + countryCode.length() > 15
                || mobilePrefix == null || !mobilePrefix.matches("[0-9]+")
                || mobilePrefix.length() > nationalLength) {
            throw new IllegalArgumentException("Invalid phone rules configuration");
        }
    }
}
