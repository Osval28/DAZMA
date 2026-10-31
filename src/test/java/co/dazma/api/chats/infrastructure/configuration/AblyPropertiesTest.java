package co.dazma.api.chats.infrastructure.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class AblyPropertiesTest {

    private static final String API_KEY = "dazma.chats.ably.api-key=app.key-name:key-secret";
    private static final String TOKEN_VALIDITY = "dazma.chats.ably.token-validity=1h";
    private static final String ROOM_NAMESPACE = "dazma.chats.ably.room-namespace=dazma-chat";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(PropertiesConfiguration.class);

    @Test
    void shouldBindAValidConfiguration() {
        contextRunner
                .withPropertyValues(API_KEY, TOKEN_VALIDITY, ROOM_NAMESPACE)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    AblyProperties properties = context.getBean(AblyProperties.class);
                    assertThat(properties.keyName()).isEqualTo("app.key-name");
                    assertThat(properties.keySecret()).isEqualTo("key-secret");
                    assertThat(properties.tokenValidity()).isEqualTo(Duration.ofHours(1));
                    assertThat(properties.roomNamespace()).isEqualTo("dazma-chat");
                });
    }

    @Test
    void shouldFailWhenTheApiKeyIsMissing() {
        contextRunner
                .withPropertyValues(TOKEN_VALIDITY, ROOM_NAMESPACE)
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void shouldFailWhenTheApiKeyHasNoSecret() {
        contextRunner
                .withPropertyValues("dazma.chats.ably.api-key=app.key-name", TOKEN_VALIDITY, ROOM_NAMESPACE)
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void shouldFailWhenTheTokenValidityIsTooShort() {
        contextRunner
                .withPropertyValues(API_KEY, "dazma.chats.ably.token-validity=10s", ROOM_NAMESPACE)
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void shouldFailWhenTheRoomNamespaceHasInvalidCharacters() {
        contextRunner
                .withPropertyValues(API_KEY, TOKEN_VALIDITY, "dazma.chats.ably.room-namespace=Dazma Chat:")
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void shouldNotExposeTheSecretWhenPrinted() {
        contextRunner
                .withPropertyValues(API_KEY, TOKEN_VALIDITY, ROOM_NAMESPACE)
                .run(context -> assertThat(context.getBean(AblyProperties.class).toString())
                        .contains("app.key-name")
                        .doesNotContain("key-secret"));
    }

    @EnableConfigurationProperties(AblyProperties.class)
    static class PropertiesConfiguration {
    }
}