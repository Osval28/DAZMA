package co.dazma.api.notifications.infrastructure.output.externalservices.resend;

import java.util.function.Predicate;

public final class TransientNotificationFailurePredicate implements Predicate<Throwable> {

    @Override
    public boolean test(Throwable failure) {
        return failure instanceof NotificationDeliveryException deliveryFailure
                && deliveryFailure.isRetryable();
    }
}
