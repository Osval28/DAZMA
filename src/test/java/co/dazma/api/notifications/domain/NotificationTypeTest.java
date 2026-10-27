package co.dazma.api.notifications.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificationTypeTest {

    @Test
    void shouldExposeAKebabCaseKey() {
        assertEquals("publication-created", NotificationType.PUBLICATION_CREATED.key());
    }
}
