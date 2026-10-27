package co.dazma.api.notifications.services.input.adapters.mappers;

import co.dazma.api.notifications.domain.EmailNotification;
import co.dazma.api.notifications.services.input.dto.SendEmailInputDto;
import co.dazma.api.shared.services.input.mappers.InputMapper;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationMapper implements InputMapper<SendEmailInputDto, EmailNotification> {

    @Override
    public EmailNotification toDomain(SendEmailInputDto input) {
        return new EmailNotification(
                input.type(),
                input.recipientUserId(),
                input.referenceId(),
                input.data()
        );
    }
}
