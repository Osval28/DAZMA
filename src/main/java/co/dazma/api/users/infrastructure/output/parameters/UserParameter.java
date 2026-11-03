package co.dazma.api.users.infrastructure.output.parameters;

import co.dazma.api.shared.services.output.ports.ParameterKey;

public enum UserParameter implements ParameterKey {
    PHONE_COUNTRY_CODE("dazma.users.phone.country-code"),
    PHONE_NATIONAL_LENGTH("dazma.users.phone.national-length"),
    PHONE_MOBILE_PREFIX("dazma.users.phone.mobile-prefix");

    private final String key;

    UserParameter(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }
}
