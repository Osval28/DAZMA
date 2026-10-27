package co.dazma.api.users.domain;

import co.dazma.api.shared.domain.Municipality;

public record UserRegistration(
        Name name,
        Phone phone,
        Municipality municipality,
        String externalId,
        Email email
) {
}
