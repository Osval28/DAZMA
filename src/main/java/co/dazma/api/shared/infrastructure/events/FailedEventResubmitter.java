package co.dazma.api.shared.infrastructure.events;

import org.springframework.modulith.events.EventPublication;
import org.springframework.modulith.events.FailedEventPublications;
import org.springframework.modulith.events.ResubmissionOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FailedEventResubmitter {

    private final FailedEventPublications failedEventPublications;
    private final EventResubmissionProperties properties;

    public FailedEventResubmitter(
            FailedEventPublications failedEventPublications,
            EventResubmissionProperties properties
    ) {
        this.failedEventPublications = failedEventPublications;
        this.properties = properties;
    }

    @Scheduled(
            initialDelayString = "${dazma.events.resubmission.interval}",
            fixedDelayString = "${dazma.events.resubmission.interval}"
    )
    public void resubmitFailedEvents() {
        failedEventPublications.resubmit(ResubmissionOptions.defaults()
                .withBatchSize(properties.batchSize())
                .withMinAge(properties.minAge())
                .withFilter(this::hasAttemptsLeft));
    }

    boolean hasAttemptsLeft(EventPublication publication) {
        return publication.getCompletionAttempts() < properties.maxAttempts();
    }
}