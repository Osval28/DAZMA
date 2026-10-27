package co.dazma.api.notifications.domain;

import java.util.Locale;

public enum NotificationType {

    PUBLICATION_CREATED;

    public String key() {
        return name().toLowerCase(Locale.ROOT).replace('_', '-');
    }
}
