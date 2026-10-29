package co.dazma.api.notifications.infrastructure.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(EmailExecutorProperties.class)
public class EmailExecutorConfiguration {

    public static final String EMAIL_EXECUTOR = "emailTaskExecutor";

    @Bean(name = EMAIL_EXECUTOR)
    public ThreadPoolTaskExecutor emailTaskExecutor(EmailExecutorProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.threads());
        executor.setMaxPoolSize(properties.threads());
        executor.setQueueCapacity(properties.queueCapacity());
        executor.setThreadNamePrefix("dazma-email-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationMillis(properties.shutdownTimeout().toMillis());
        return executor;
    }
}