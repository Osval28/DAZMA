package co.dazma.api.users.infrastructure.input.dto;

import co.dazma.api.users.services.input.dto.PhoneRulesOutputDto;

public record PhoneRulesResponseDto(String countryCode, int nationalLength, String mobilePrefix) {

    public static PhoneRulesResponseDto from(PhoneRulesOutputDto rules) {
        return new PhoneRulesResponseDto(rules.countryCode(), rules.nationalLength(), rules.mobilePrefix());
    }
}
