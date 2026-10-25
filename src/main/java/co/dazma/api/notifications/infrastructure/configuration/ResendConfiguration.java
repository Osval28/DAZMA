package co.dazma.api.notifications.infrastructure.configuration;

import com.resend.Resend;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ResendProperties.class)
public class ResendConfiguration {

    @Bean
    public Resend resend(ResendProperties properties) {
        return Resend.builder()
                .apiKey(properties.apiKey())
                .connectTimeout(properties.connectTimeout())
                .readTimeout(properties.readTimeout())
                .build();
    }
}
