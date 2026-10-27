package co.dazma.api.publications.infrastructure.configuration;

import co.dazma.api.publications.infrastructure.output.storage.r2.CloudflareR2PublicationPhotoStorageAdapter;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import co.dazma.api.shared.infrastructure.observability.ExternalDependencyObservation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CloudflareR2ConfigurationTest {

    @Test
    void shouldBuildTheR2ClientAndPresigner() {
        CloudflareR2Properties properties = new CloudflareR2Properties(
                "https://account.r2.cloudflarestorage.com",
                "access-key",
                "secret-key",
                "publication-photos"
        );
        CloudflareR2Configuration configuration = new CloudflareR2Configuration();

        try (S3Client client = configuration.cloudflareR2Client(properties);
             S3Presigner presigner = configuration.cloudflareR2Presigner(properties)) {
            assertNotNull(client);
            assertNotNull(presigner);
            assertEquals(Region.of("auto"), client.serviceClientConfiguration().region());
            assertEquals(
                    properties.endpointUri(),
                    client.serviceClientConfiguration().endpointOverride().orElseThrow()
            );
        }
    }

    @Test
    void shouldWireTheR2StorageAdapter() {
        CloudflareR2Properties r2Properties = new CloudflareR2Properties(
                "https://account.r2.cloudflarestorage.com",
                "access-key",
                "secret-key",
                "publication-photos"
        );
        CloudflareR2Configuration configuration = new CloudflareR2Configuration();

        PublicationPhotoStoragePort storagePort = configuration.publicationPhotoStoragePort(
                mock(S3Client.class),
                mock(S3Presigner.class),
                r2Properties,
                new ExternalDependencyObservation(ObservationRegistry.NOOP)
        );

        assertInstanceOf(CloudflareR2PublicationPhotoStorageAdapter.class, storagePort);
    }

    @Test
    void shouldConfigureBoundedR2ConnectionAndSocketTimeouts() {
        CloudflareR2Properties properties = new CloudflareR2Properties(
                "https://account.r2.cloudflarestorage.com",
                "access-key",
                "secret-key",
                "publication-photos"
        );
        CloudflareR2Configuration configuration = new CloudflareR2Configuration();
        UrlConnectionHttpClient.Builder httpClientBuilder =
                mock(UrlConnectionHttpClient.Builder.class);
        SdkHttpClient httpClient = mock(SdkHttpClient.class);
        when(httpClientBuilder.connectionTimeout(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.socketTimeout(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.buildWithDefaults(any())).thenReturn(httpClient);

        try (S3Client ignored = configuration.cloudflareR2Client(
                properties,
                httpClientBuilder
        )) {
            verify(httpClientBuilder).connectionTimeout(Duration.ofSeconds(2));
            verify(httpClientBuilder).socketTimeout(Duration.ofSeconds(10));
        }
    }
}
