package co.dazma.api.publications.infrastructure.configuration;

import co.dazma.api.publications.infrastructure.output.storage.r2.CloudflareR2PublicationPhotoStorageAdapter;
import co.dazma.api.publications.services.input.ports.AuthorizePublicationPhotoUploadUseCase;
import co.dazma.api.publications.services.input.ports.CreatePublicationUseCase;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import co.dazma.api.publications.services.output.ports.PublicationRepositoryPort;
import co.dazma.api.publications.services.usecases.AuthorizePublicationPhotoUploadService;
import co.dazma.api.publications.services.usecases.CreatePublicationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
        prefix = "dazma.storage.r2",
        name = "enabled",
        havingValue = "true"
)
@EnableConfigurationProperties({
        CloudflareR2Properties.class,
        PublicationPhotoUploadProperties.class
})
public class CloudflareR2Configuration {

    private static final Region R2_REGION = Region.of("auto");

    @Bean(destroyMethod = "close")
    public S3Client cloudflareR2Client(CloudflareR2Properties properties) {
        return S3Client.builder()
                .endpointOverride(properties.endpoint())
                .credentialsProvider(credentialsProvider(properties))
                .region(R2_REGION)
                .serviceConfiguration(r2ServiceConfiguration())
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .build();
    }

    @Bean(destroyMethod = "close")
    public S3Presigner cloudflareR2Presigner(CloudflareR2Properties properties) {
        return S3Presigner.builder()
                .endpointOverride(properties.endpoint())
                .credentialsProvider(credentialsProvider(properties))
                .region(R2_REGION)
                .serviceConfiguration(r2ServiceConfiguration())
                .build();
    }

    @Bean
    public PublicationPhotoStoragePort publicationPhotoStoragePort(
            S3Client cloudflareR2Client,
            S3Presigner cloudflareR2Presigner,
            CloudflareR2Properties properties
    ) {
        return new CloudflareR2PublicationPhotoStorageAdapter(
                cloudflareR2Client,
                cloudflareR2Presigner,
                properties.bucketName()
        );
    }

    @Bean
    public AuthorizePublicationPhotoUploadUseCase authorizePublicationPhotoUploadUseCase(
            PublicationPhotoStoragePort photoStorage,
            PublicationPhotoUploadProperties properties
    ) {
        return new AuthorizePublicationPhotoUploadService(
                photoStorage,
                properties.allowedContentTypes(),
                properties.maximumContentLength(),
                properties.validity()
        );
    }

    @Bean
    public CreatePublicationUseCase createPublicationUseCase(
            PublicationRepositoryPort publicationRepository,
            PublicationPhotoStoragePort photoStorage,
            Clock clock
    ) {
        return new CreatePublicationService(publicationRepository, photoStorage, clock);
    }

    private StaticCredentialsProvider credentialsProvider(
            CloudflareR2Properties properties
    ) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                properties.accessKeyId(),
                properties.secretAccessKey()
        );
        return StaticCredentialsProvider.create(credentials);
    }

    private S3Configuration r2ServiceConfiguration() {
        return S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .chunkedEncodingEnabled(false)
                .build();
    }
}
