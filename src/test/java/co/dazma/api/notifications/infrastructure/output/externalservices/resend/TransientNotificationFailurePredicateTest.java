package co.dazma.api.notifications.infrastructure.output.externalservices.resend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransientNotificationFailurePredicateTest {

    private final TransientNotificationFailurePredicate predicate =
            new TransientNotificationFailurePredicate();

    @Test
    void shouldAcceptOnlyRetryableDeliveryFailures() {
        assertTrue(predicate.test(new NotificationDeliveryException(503, true, null)));
        assertFalse(predicate.test(new NotificationDeliveryException(400, false, null)));
        assertFalse(predicate.test(new IllegalStateException("unexpected")));
    }
}
