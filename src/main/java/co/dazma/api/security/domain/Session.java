package co.dazma.api.security.domain;

import java.time.Duration;

public record Session(String token, Duration validity) {
}
