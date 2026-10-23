package co.dazma.api.security.infrastructure.input.cookies;

import org.springframework.http.ResponseCookie;

import java.time.Duration;

public final class SessionCookies {

    public static final String NAME = "dazma_session";

    private SessionCookies() {
    }

    public static ResponseCookie open(String sessionToken, Duration validity) {
        return base(sessionToken).maxAge(validity).build();
    }

    public static ResponseCookie expired() {
        return base("").maxAge(Duration.ZERO).build();
    }

    private static ResponseCookie.ResponseCookieBuilder base(String value) {
        return ResponseCookie.from(NAME, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/");
    }
}
