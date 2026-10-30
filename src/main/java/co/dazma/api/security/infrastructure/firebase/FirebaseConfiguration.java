package co.dazma.api.security.infrastructure.firebase;

import co.dazma.api.security.infrastructure.input.filters.FirebaseAuthenticationFilter;
import co.dazma.api.security.infrastructure.output.firebase.FirebaseIdentityTokenVerifier;
import co.dazma.api.security.infrastructure.output.firebase.FirebaseSessionTokenIssuer;
import co.dazma.api.security.services.input.adapters.AuthenticateIdentityInputAdapter;
import co.dazma.api.security.services.input.adapters.CloseSessionInputAdapter;
import co.dazma.api.security.services.input.adapters.OpenSessionInputAdapter;
import co.dazma.api.security.services.input.adapters.mappers.AuthenticatedIdentityMapper;
import co.dazma.api.security.services.input.adapters.mappers.SessionMapper;
import co.dazma.api.security.services.input.ports.AuthenticateIdentityInputPort;
import co.dazma.api.security.services.input.ports.CloseSessionInputPort;
import co.dazma.api.security.services.input.ports.OpenSessionInputPort;
import co.dazma.api.security.services.output.ports.IdentityTokenVerifier;
import co.dazma.api.security.services.output.ports.SessionTokenIssuer;
import co.dazma.api.security.services.usecases.contract.AuthenticateIdentityUseCase;
import co.dazma.api.security.services.usecases.contract.CloseSessionUseCase;
import co.dazma.api.security.services.usecases.contract.OpenSessionUseCase;
import co.dazma.api.security.services.usecases.impl.AuthenticateIdentityService;
import co.dazma.api.security.services.usecases.impl.CloseSessionService;
import co.dazma.api.security.services.usecases.impl.OpenSessionService;
import co.dazma.api.shared.infrastructure.observability.ExternalDependencyObservation;
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
import java.time.Clock;
import java.time.Duration;

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
    public IdentityTokenVerifier identityTokenVerifier(
            FirebaseAuth firebaseAuth,
            ExternalDependencyObservation observations
    ) {
        return new FirebaseIdentityTokenVerifier(firebaseAuth, observations);
    }

    @Bean
    public SessionTokenIssuer sessionTokenIssuer(
            FirebaseAuth firebaseAuth,
            ExternalDependencyObservation observations
    ) {
        return new FirebaseSessionTokenIssuer(firebaseAuth, observations);
    }

    @Bean
    public OpenSessionUseCase openSessionUseCase(
            SessionTokenIssuer sessionTokenIssuer,
            @Value("${dazma.session.validity:PT8H}") Duration sessionValidity,
            @Value("${dazma.session.maximum-sign-in-age:PT5M}") Duration maximumSignInAge,
            Clock clock
    ) {
        return new OpenSessionService(sessionTokenIssuer, sessionValidity, maximumSignInAge, clock);
    }

    @Bean
    public CloseSessionUseCase closeSessionUseCase(
            IdentityTokenVerifier identityTokenVerifier,
            SessionTokenIssuer sessionTokenIssuer
    ) {
        return new CloseSessionService(identityTokenVerifier, sessionTokenIssuer);
    }

    @Bean
    public AuthenticateIdentityUseCase authenticateIdentityUseCase(
            IdentityTokenVerifier identityTokenVerifier
    ) {
        return new AuthenticateIdentityService(identityTokenVerifier);
    }

    @Bean
    public OpenSessionInputPort openSessionInputPort(OpenSessionUseCase openSessionUseCase) {
        return new OpenSessionInputAdapter(openSessionUseCase, new SessionMapper());
    }

    @Bean
    public CloseSessionInputPort closeSessionInputPort(CloseSessionUseCase closeSessionUseCase) {
        return new CloseSessionInputAdapter(closeSessionUseCase);
    }

    @Bean
    public AuthenticateIdentityInputPort authenticateIdentityInputPort(
            AuthenticateIdentityUseCase authenticateIdentityUseCase
    ) {
        return new AuthenticateIdentityInputAdapter(authenticateIdentityUseCase, new AuthenticatedIdentityMapper());
    }

    @Bean
    public FirebaseAuthenticationFilter firebaseAuthenticationFilter(
            AuthenticateIdentityInputPort authenticateIdentityInputPort
    ) {
        return new FirebaseAuthenticationFilter(authenticateIdentityInputPort);
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
