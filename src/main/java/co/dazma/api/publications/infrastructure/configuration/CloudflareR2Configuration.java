package co.dazma.api.publications.infrastructure.configuration;

import co.dazma.api.publications.infrastructure.output.storage.r2.CloudflareR2PublicationPhotoStorageAdapter;
import co.dazma.api.publications.services.output.ports.PublicationPhotoStoragePort;
import co.dazma.api.shared.infrastructure.observability.ExternalDependencyObservation;
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

import java.time.Duration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CloudflareR2Properties.class)
public class CloudflareR2Configuration {

    private static final Region R2_REGION = Region.of("auto");
    private static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(2);
    private static final Duration SOCKET_TIMEOUT = Duration.ofSeconds(10);

    @Bean(destroyMethod = "close")
    public S3Client cloudflareR2Client(CloudflareR2Properties properties) {
        return cloudflareR2Client(properties, UrlConnectionHttpClient.builder());
    }

    S3Client cloudflareR2Client(
            CloudflareR2Properties properties,
            UrlConnectionHttpClient.Builder httpClientBuilder
    ) {
        return S3Client.builder()
                .endpointOverride(properties.endpointUri())
                .credentialsProvider(credentialsProvider(properties))
                .region(R2_REGION)
                .serviceConfiguration(r2ServiceConfiguration())
                .httpClientBuilder(httpClientBuilder
                        .connectionTimeout(CONNECTION_TIMEOUT)
                        .socketTimeout(SOCKET_TIMEOUT))
                .build();
    }

    @Bean(destroyMethod = "close")
    public S3Presigner cloudflareR2Presigner(CloudflareR2Properties properties) {
        return S3Presigner.builder()
                .endpointOverride(properties.endpointUri())
                .credentialsProvider(credentialsProvider(properties))
                .region(R2_REGION)
                .serviceConfiguration(r2ServiceConfiguration())
                .build();
    }

    @Bean
    public PublicationPhotoStoragePort publicationPhotoStoragePort(
            S3Client cloudflareR2Client,
            S3Presigner cloudflareR2Presigner,
            CloudflareR2Properties properties,
            ExternalDependencyObservation observations
    ) {
        return new CloudflareR2PublicationPhotoStorageAdapter(
                cloudflareR2Client,
                cloudflareR2Presigner,
                properties.bucketName(),
                observations
        );
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
