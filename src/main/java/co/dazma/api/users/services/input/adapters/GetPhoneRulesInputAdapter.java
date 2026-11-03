package co.dazma.api.users.services.input.adapters;

import co.dazma.api.users.services.input.dto.PhoneRulesOutputDto;
import co.dazma.api.users.services.input.ports.GetPhoneRulesInputPort;
import co.dazma.api.users.services.output.ports.PhoneRulesPort;
import org.springframework.stereotype.Service;

@Service
public class GetPhoneRulesInputAdapter implements GetPhoneRulesInputPort {

    private final PhoneRulesPort rules;

    public GetPhoneRulesInputAdapter(PhoneRulesPort rules) {
        this.rules = rules;
    }

    @Override
    public PhoneRulesOutputDto execute() {
        var current = rules.current();
        return new PhoneRulesOutputDto(current.countryCode(), current.nationalLength(), current.mobilePrefix());
    }
}
