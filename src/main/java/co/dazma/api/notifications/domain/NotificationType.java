package co.dazma.api.notifications.domain;

import java.util.Locale;

public enum NotificationType {

    ACCOUNT_CREATED,
    PUBLICATION_CREATED;

    public String key() {
        return name().toLowerCase(Locale.ROOT).replace('_', '-');
    }
}
