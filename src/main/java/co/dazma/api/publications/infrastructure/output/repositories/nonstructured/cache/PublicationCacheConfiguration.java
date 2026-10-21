package co.dazma.api.publications.infrastructure.output.repositories.nonstructured.cache;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PublicationCacheProperties.class)
public class PublicationCacheConfiguration {
}