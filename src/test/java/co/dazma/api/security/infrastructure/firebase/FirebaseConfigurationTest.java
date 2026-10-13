package co.dazma.api.security.infrastructure.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FirebaseConfigurationTest {

    private final FirebaseConfiguration configuration = new FirebaseConfiguration();

    @Test
    void shouldInitializeFirebaseWhenThereIsNoExistingApplication() throws IOException {
        GoogleCredentials credentials = mock(GoogleCredentials.class);
        FirebaseApp expectedApp = mock(FirebaseApp.class);
        FirebaseOptions options = mock(FirebaseOptions.class);
        FirebaseOptions.Builder optionsBuilder = mock(FirebaseOptions.Builder.class);

        try (MockedStatic<GoogleCredentials> credentialsMock = mockStatic(GoogleCredentials.class);
             MockedStatic<FirebaseOptions> firebaseOptionsMock = mockStatic(FirebaseOptions.class);
             MockedStatic<FirebaseApp> firebaseAppMock = mockStatic(FirebaseApp.class)) {
            credentialsMock.when(GoogleCredentials::getApplicationDefault).thenReturn(credentials);
            firebaseOptionsMock.when(FirebaseOptions::builder).thenReturn(optionsBuilder);
            when(optionsBuilder.setCredentials(credentials)).thenReturn(optionsBuilder);
            when(optionsBuilder.build()).thenReturn(options);
            firebaseAppMock.when(FirebaseApp::getApps).thenReturn(List.of());
            firebaseAppMock.when(() -> FirebaseApp.initializeApp(options)).thenReturn(expectedApp);

            FirebaseApp result = configuration.firebaseApp();

            assertSame(expectedApp, result);
            credentialsMock.verify(GoogleCredentials::getApplicationDefault);
            verify(optionsBuilder).setCredentials(credentials);
            firebaseAppMock.verify(() -> FirebaseApp.initializeApp(options));
        }
    }

    @Test
    void shouldReuseFirebaseWhenAnApplicationAlreadyExists() throws IOException {
        FirebaseApp existingApp = mock(FirebaseApp.class);

        try (MockedStatic<GoogleCredentials> credentialsMock = mockStatic(GoogleCredentials.class);
             MockedStatic<FirebaseApp> firebaseAppMock = mockStatic(FirebaseApp.class)) {
            firebaseAppMock.when(FirebaseApp::getApps).thenReturn(List.of(existingApp));
            firebaseAppMock.when(FirebaseApp::getInstance).thenReturn(existingApp);

            FirebaseApp result = configuration.firebaseApp();

            assertSame(existingApp, result);
            credentialsMock.verify(GoogleCredentials::getApplicationDefault, never());
            firebaseAppMock.verify(FirebaseApp::getInstance);
        }
    }

    @Test
    void shouldCreateFirebaseAuthFromTheConfiguredApplication() {
        FirebaseApp firebaseApp = mock(FirebaseApp.class);
        FirebaseAuth expectedAuth = mock(FirebaseAuth.class);

        try (MockedStatic<FirebaseAuth> firebaseAuthMock = mockStatic(FirebaseAuth.class)) {
            firebaseAuthMock.when(() -> FirebaseAuth.getInstance(firebaseApp)).thenReturn(expectedAuth);

            FirebaseAuth result = configuration.firebaseAuth(firebaseApp);

            assertSame(expectedAuth, result);
            firebaseAuthMock.verify(() -> FirebaseAuth.getInstance(firebaseApp));
        }
    }
}
