package co.dazma.api.notifications.services.input.dto;

import java.util.UUID;

public record RegisterRecipientInputDto(UUID userId, String email) {
}
