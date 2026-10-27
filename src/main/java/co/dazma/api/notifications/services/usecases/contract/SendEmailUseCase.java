package co.dazma.api.notifications.services.usecases.contract;

import co.dazma.api.notifications.domain.EmailNotification;
import co.dazma.api.shared.services.usecases.UseCaseWithoutReturn;

public interface SendEmailUseCase extends UseCaseWithoutReturn<EmailNotification> {
}
