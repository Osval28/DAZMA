package co.dazma.api.shared.infrastructure.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.events.EventPublication;
import org.springframework.modulith.events.FailedEventPublications;
import org.springframework.modulith.events.ResubmissionOptions;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FailedEventResubmitterTest {

    private FailedEventPublications failedEventPublications;
    private FailedEventResubmitter resubmitter;

    @BeforeEach
    void setUp() {
        failedEventPublications = mock(FailedEventPublications.class);
        resubmitter = new FailedEventResubmitter(
                failedEventPublications,
                new EventResubmissionProperties(Duration.ofMinutes(2), 5, 50)
        );
    }

    @Test
    void shouldAskModulithToResubmitTheFailedPublications() {
        resubmitter.resubmitFailedEvents();

        verify(failedEventPublications).resubmit(any(ResubmissionOptions.class));
    }

    @Test
    void shouldRetryAPublicationThatStillHasAttemptsLeft() {
        assertTrue(resubmitter.hasAttemptsLeft(publicationWithAttempts(4)));
    }

    @Test
    void shouldStopRetryingAPublicationThatUsedAllItsAttempts() {
        assertFalse(resubmitter.hasAttemptsLeft(publicationWithAttempts(5)));
    }

    private static EventPublication publicationWithAttempts(int attempts) {
        EventPublication publication = mock(EventPublication.class);
        when(publication.getCompletionAttempts()).thenReturn(attempts);
        return publication;
    }
}
