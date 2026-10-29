package co.dazma.api.security.infrastructure.configuration;

import co.dazma.api.security.infrastructure.input.errors.SecurityProblemWriter;
import co.dazma.api.security.infrastructure.input.filters.OriginSecretFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "dazma.security.origin.enabled", havingValue = "true")
@EnableConfigurationProperties(OriginSecretProperties.class)
public class OriginSecretConfiguration {

    static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 20;

    @Bean
    public FilterRegistrationBean<OriginSecretFilter> originSecretFilterRegistration(
            OriginSecretProperties properties,
            SecurityProblemWriter problemWriter
    ) {
        FilterRegistrationBean<OriginSecretFilter> registration =
                new FilterRegistrationBean<>(new OriginSecretFilter(properties.secrets(), problemWriter));
        registration.setOrder(ORDER);
        return registration;
    }
}
