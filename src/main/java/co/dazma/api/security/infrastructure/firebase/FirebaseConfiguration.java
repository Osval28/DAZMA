package co.dazma.api.security.infrastructure.firebase;

import co.dazma.api.security.infrastructure.input.filters.FirebaseAuthenticationFilter;
import co.dazma.api.security.infrastructure.input.errors.ApiAuthenticationEntryPoint;
import co.dazma.api.security.infrastructure.output.firebase.FirebaseIdentityTokenVerifier;
import co.dazma.api.security.services.input.ports.AuthenticateIdentityUseCase;
import co.dazma.api.security.services.output.ports.IdentityTokenVerifier;
import co.dazma.api.security.services.usecases.AuthenticateIdentityService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@ConditionalOnProperty(
        name = "firebase.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class FirebaseConfiguration {

    @Bean
    public FirebaseApp firebaseApp(
            @Value("${dazma.firebase.service-account}") String serviceAccountJson
    ) throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentialsFrom(serviceAccountJson))
                .build();

        return FirebaseApp.initializeApp(options);
    }
    private GoogleCredentials credentialsFrom(String serviceAccountJson) throws IOException {
        var serviceAccountStream = new ByteArrayInputStream(
                serviceAccountJson.getBytes(StandardCharsets.UTF_8)
        );
        return GoogleCredentials.fromStream(serviceAccountStream);
    }
    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }

    @Bean
    public IdentityTokenVerifier identityTokenVerifier(FirebaseAuth firebaseAuth) {
        return new FirebaseIdentityTokenVerifier(firebaseAuth);
    }

    @Bean
    public AuthenticateIdentityUseCase authenticateIdentityUseCase(
            IdentityTokenVerifier identityTokenVerifier
    ) {
        return new AuthenticateIdentityService(identityTokenVerifier);
    }

    @Bean
    public FirebaseAuthenticationFilter firebaseAuthenticationFilter(
            AuthenticateIdentityUseCase authenticateIdentityUseCase,
            ApiAuthenticationEntryPoint authenticationEntryPoint
    ) {
        return new FirebaseAuthenticationFilter(
                authenticateIdentityUseCase,
                authenticationEntryPoint
        );
    }

    @Bean
    public FilterRegistrationBean<FirebaseAuthenticationFilter> firebaseAuthenticationFilterRegistration(
            FirebaseAuthenticationFilter filter
    ) {
        FilterRegistrationBean<FirebaseAuthenticationFilter> registration =
                new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
