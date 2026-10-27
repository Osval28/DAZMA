package co.dazma.api.notifications.infrastructure.output.templates;

import co.dazma.api.notifications.domain.NotificationType;
import co.dazma.api.notifications.services.output.models.NotificationContent;
import co.dazma.api.notifications.services.output.ports.NotificationTemplateCatalog;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

@Component
public class ClasspathNotificationTemplateCatalog implements NotificationTemplateCatalog {

    private static final String TEMPLATES_FOLDER = "templates/notifications/";
    private static final String PLACEHOLDER_START = "{{";

    private final Map<NotificationType, NotificationContent> templates = new EnumMap<>(NotificationType.class);

    public ClasspathNotificationTemplateCatalog() {
        for (NotificationType type : NotificationType.values()) {
            templates.put(type, new NotificationContent(
                    read(type, "subject.txt").strip(),
                    read(type, "body.html")
            ));
        }
    }

    @Override
    public NotificationContent render(NotificationType type, Map<String, String> data) {
        NotificationContent template = templates.get(type);
        String subject = template.subject();
        String body = template.body();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String placeholder = PLACEHOLDER_START + entry.getKey() + "}}";
            subject = subject.replace(placeholder, entry.getValue());
            body = body.replace(placeholder, HtmlUtils.htmlEscape(entry.getValue()));
        }
        if (subject.contains(PLACEHOLDER_START) || body.contains(PLACEHOLDER_START)) {
            throw new NotificationTemplateDataMissingException(type.key());
        }
        return new NotificationContent(subject, body);
    }

    private static String read(NotificationType type, String file) {
        ClassPathResource resource = new ClassPathResource(TEMPLATES_FOLDER + type.key() + "/" + file);
        try {
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new NotificationTemplateNotFoundException(resource.getPath(), type.key(), exception);
        }
    }
}
