package co.dazma.api.users.domain.models;

import co.dazma.api.shared.domain.models.Municipality;

public record UserRegistration(
        Name name,
        Phone phone,
        Municipality municipality,
        String externalId,
        Email email
) {
}
