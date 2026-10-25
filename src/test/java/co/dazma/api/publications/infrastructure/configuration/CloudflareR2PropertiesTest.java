package co.dazma.api.publications.infrastructure.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class CloudflareR2PropertiesTest {

    private static final String ENDPOINT = "dazma.storage.r2.endpoint=https://account.r2.cloudflarestorage.com";
    private static final String ACCESS_KEY_ID = "dazma.storage.r2.access-key-id=access-key";
    private static final String SECRET_ACCESS_KEY = "dazma.storage.r2.secret-access-key=secret-key";
    private static final String BUCKET_NAME = "dazma.storage.r2.bucket-name=publication-photos";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(PropertiesConfiguration.class);

    @Test
    void shouldBindAValidConfiguration() {
        contextRunner
                .withPropertyValues(ENDPOINT, ACCESS_KEY_ID, SECRET_ACCESS_KEY, BUCKET_NAME)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    CloudflareR2Properties properties = context.getBean(CloudflareR2Properties.class);
                    assertThat(properties.endpointUri())
                            .isEqualTo(URI.create("https://account.r2.cloudflarestorage.com"));
                    assertThat(properties.accessKeyId()).isEqualTo("access-key");
                    assertThat(properties.secretAccessKey()).isEqualTo("secret-key");
                    assertThat(properties.bucketName()).isEqualTo("publication-photos");
                });
    }

    @Test
    void shouldFailWhenAPropertyIsMissing() {
        contextRunner
                .withPropertyValues(ENDPOINT, ACCESS_KEY_ID, SECRET_ACCESS_KEY)
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void shouldFailWhenAPropertyIsBlank() {
        contextRunner
                .withPropertyValues(ENDPOINT, ACCESS_KEY_ID, SECRET_ACCESS_KEY, "dazma.storage.r2.bucket-name= ")
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void shouldFailWhenTheEndpointIsNotAnHttpsAddress() {
        contextRunner
                .withPropertyValues(
                        "dazma.storage.r2.endpoint=relative-endpoint",
                        ACCESS_KEY_ID,
                        SECRET_ACCESS_KEY,
                        BUCKET_NAME
                )
                .run(context -> assertThat(context).hasFailed());
        contextRunner
                .withPropertyValues(
                        "dazma.storage.r2.endpoint=http://account.r2.cloudflarestorage.com",
                        ACCESS_KEY_ID,
                        SECRET_ACCESS_KEY,
                        BUCKET_NAME
                )
                .run(context -> assertThat(context).hasFailed());
    }

    @EnableConfigurationProperties(CloudflareR2Properties.class)
    static class PropertiesConfiguration {
    }
}
