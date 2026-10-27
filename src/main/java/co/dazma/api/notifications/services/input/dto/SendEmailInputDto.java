package co.dazma.api.notifications.services.input.dto;

import co.dazma.api.notifications.domain.NotificationType;

import java.util.Map;
import java.util.UUID;

public record SendEmailInputDto(
        NotificationType type,
        UUID recipientUserId,
        UUID referenceId,
        Map<String, String> data
) {
}
