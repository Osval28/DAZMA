package co.dazma.api.users.events;

import java.util.UUID;

public record UserRegistered(
        UUID userId,
        String email
) {
}
