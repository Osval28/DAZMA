package co.dazma.api.publications.infrastructure.configuration;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CloudflareR2PropertiesTest {

    @Test
    void shouldStoreAValidConfiguration() {
        CloudflareR2Properties properties = new CloudflareR2Properties(
                URI.create("https://account.r2.cloudflarestorage.com"),
                " access-key ",
                " secret-key ",
                " publication-photos "
        );

        assertEquals(
                URI.create("https://account.r2.cloudflarestorage.com"),
                properties.endpoint()
        );
        assertEquals("access-key", properties.accessKeyId());
        assertEquals("secret-key", properties.secretAccessKey());
        assertEquals("publication-photos", properties.bucketName());
    }

    @Test
    void shouldRejectIncompleteConfiguration() {
        assertThrows(
                NullPointerException.class,
                () -> new CloudflareR2Properties(null, "key", "secret", "bucket")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new CloudflareR2Properties(
                        URI.create("relative-endpoint"),
                        "key",
                        "secret",
                        "bucket"
                )
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new CloudflareR2Properties(
                        URI.create("https://account.r2.cloudflarestorage.com"),
                        " ",
                        "secret",
                        "bucket"
                )
        );
    }
}
