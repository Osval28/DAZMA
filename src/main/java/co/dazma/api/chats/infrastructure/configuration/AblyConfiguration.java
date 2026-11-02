package co.dazma.api.chats.infrastructure.configuration;

import co.dazma.api.chats.infrastructure.output.externalservices.ably.AblyChatTokenIssuerAdapter;
import co.dazma.api.chats.services.output.ports.ChatTokenIssuerPort;
import io.ably.lib.rest.AblyRest;
import io.ably.lib.types.AblyException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AblyProperties.class)
public class AblyConfiguration {

    @Bean(destroyMethod = "close")
    public AblyRest ablyRest(AblyProperties properties) throws AblyException {
        return new AblyRest(properties.apiKey());
    }

    @Bean
    public ChatTokenIssuerPort chatTokenIssuerPort(AblyRest ablyRest, AblyProperties properties) {
        return new AblyChatTokenIssuerAdapter(ablyRest, properties);
    }
}