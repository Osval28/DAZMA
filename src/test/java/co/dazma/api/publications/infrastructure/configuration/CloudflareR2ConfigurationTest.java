package co.dazma.api.publications.infrastructure.configuration;

import co.dazma.api.publications.infrastructure.output.storage.r2.CloudflareR2PublicationPhotoStorageAdapter;
import co.dazma.api.publications.services.input.ports.AuthorizePublicationPhotoUploadUseCase;
import co.dazma.api.publications.services.input.ports.CreatePublicationUseCase;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import co.dazma.api.publications.services.output.ports.PublicationRepositoryPort;
import co.dazma.api.publications.services.usecases.AuthorizePublicationPhotoUploadService;
import co.dazma.api.publications.services.usecases.CreatePublicationService;
import org.junit.jupiter.api.Test;
import org.springframework.util.unit.DataSize;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.time.Duration;
import java.time.Clock;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class CloudflareR2ConfigurationTest {

    @Test
    void shouldBuildTheR2ClientAndPresigner() {
        CloudflareR2Properties properties = new CloudflareR2Properties(
                URI.create("https://account.r2.cloudflarestorage.com"),
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
                    properties.endpoint(),
                    client.serviceClientConfiguration().endpointOverride().orElseThrow()
            );
        }
    }

    @Test
    void shouldWireTheR2AdapterAndUploadUseCase() {
        CloudflareR2Properties r2Properties = new CloudflareR2Properties(
                URI.create("https://account.r2.cloudflarestorage.com"),
                "access-key",
                "secret-key",
                "publication-photos"
        );
        PublicationPhotoUploadProperties uploadProperties =
                new PublicationPhotoUploadProperties(
                        Set.of("image/jpeg", "image/png"),
                        DataSize.ofMegabytes(5),
                        Duration.ofMinutes(5)
                );
        CloudflareR2Configuration configuration = new CloudflareR2Configuration();

        PublicationPhotoStoragePort storagePort = configuration.publicationPhotoStoragePort(
                mock(S3Client.class),
                mock(S3Presigner.class),
                r2Properties
        );
        AuthorizePublicationPhotoUploadUseCase useCase =
                configuration.authorizePublicationPhotoUploadUseCase(
                        storagePort,
                        uploadProperties
                );
        CreatePublicationUseCase createUseCase = configuration.createPublicationUseCase(
                mock(PublicationRepositoryPort.class),
                storagePort,
                Clock.systemUTC()
        );

        assertInstanceOf(CloudflareR2PublicationPhotoStorageAdapter.class, storagePort);
        assertInstanceOf(AuthorizePublicationPhotoUploadService.class, useCase);
        assertInstanceOf(CreatePublicationService.class, createUseCase);
    }
}
